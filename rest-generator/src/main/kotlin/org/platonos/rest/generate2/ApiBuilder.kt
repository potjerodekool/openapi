package org.platonos.rest.generate2

import com.reprezen.kaizen.oasparser.model3.*
import org.platonos.rest.openapi.api.*
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.PrimitiveType
import org.platonos.rest.generate.type.Type
import org.platonos.rest.generate.type.TypeKind
import org.platonos.rest.openapi.*

class ApiBuilder(private val config: OpenApiGeneratorConfiguration,
                 private val typeConverter: TypeConverter,
                 private val namingStrategy: ModelNamingStrategy) {

    fun buildModels(openApi: OpenApi3): Api {
        val paths = mutableMapOf<String, ApiPath>()
        val api = Api(paths)

        openApi.paths.forEach { (url, path) ->
            val getOperation = processOperation(path.get, HttpMethod.GET, api)
            val postOperation = processOperation(path.post, HttpMethod.POST, api)
            val patchOperation = processOperation(path.patch, HttpMethod.PATCH, api)
            val putOperation = processOperation(path.put, HttpMethod.PUT, api)
            val deleteOperation = processOperation(path.delete, HttpMethod.DELETE, api)

            val apiPath = ApiPath(
                get = getOperation,
                post = postOperation,
                patch = patchOperation,
                put = putOperation,
                delete = deleteOperation
            )

            paths[url] = apiPath
        }

        return api
    }

    private fun processOperation(
        operation: Operation?,
        httpMethod: HttpMethod,
        api: Api
    ): ApiOperation? {
        if (operation == null) {
            return null
        }

        val apiRequestBody = processRequest(
            httpMethod,
            operation.requestBody,
            api
        )

        val responses = processResponses(
            httpMethod,
            operation.responses,
            api
        )

        val parameters = processParameters(operation.parameters)

        return ApiOperation(
            operationId = operation.operationId,
            summary = operation.summary,
            description = operation.description,
            tags = operation.tags,
            requestBody = apiRequestBody,
            responses = responses,
            parameters = parameters)
    }

    private fun processParameters(parameters: MutableList<Parameter>): List<ApiParameter> {
        return parameters.map { processParameter(it) }
    }

    private fun processParameter(parameter: Parameter): ApiParameter {
        val schema = parameter.schema

        val type = typeConverter.convert(schema, isRequest = false)

        return ApiParameter(
            parameter.name,
            ApiParameterLocation.valueOf(parameter.`in`.toUpperCase()),
            parameter.description,
            parameter.isRequired,
            parameter.isDeprecated,
            type
        )
    }

    private fun processRequest(httpMethod: HttpMethod, requestBody: RequestBody?, api: Api): ApiRequestBody? {
        if (requestBody == null) {
            return null
        }

        val contentMediaTypes = mutableMapOf<String, ApiModel>()

        requestBody.contentMediaTypes.forEach { (contentType, mediaType) ->
            val apiModel = processSchema(httpMethod, mediaType.schema, true, api)

            if (apiModel == null) {
                throw Exception("Failed to create model for $httpMethod request with content type $contentType")
            }

            contentMediaTypes[contentType] = apiModel
        }

        return ApiRequestBody(contentMediaTypes)
    }

    private fun processResponses(
        httMethod: HttpMethod,
        responses: Map<String, Response>,
        api: Api
    ): Map<String, ApiResponse> {
        val apiResponses = mutableMapOf<String, ApiResponse>()

        responses.forEach { (code, response) ->
            val apiResponse = processResponse(
                httMethod,
                response,
                api
            )
            apiResponses[code] = apiResponse
        }

        return apiResponses
    }

    private fun processResponse(
        httMethod: HttpMethod,
        response: Response,
        api: Api
    ): ApiResponse {

        val contentMediaTypes = mutableMapOf<String, ApiMediaType>()

        response.contentMediaTypes.forEach { (contentType, mediaType) ->
            val openApiModel = processSchema(
                httMethod,
                mediaType.schema,
                false,
                api
            )

            if (openApiModel == null) {
                throw Exception("Failed to create model for $httMethod request with content type $contentType")
            }

            contentMediaTypes[contentType] = ApiMediaType(openApiModel)
        }

        return ApiResponse(
            response.description ?: "",
            contentMediaTypes
        )
    }

    private fun processSchema(
        httMethod: HttpMethod,
        schema: Schema,
        isRequest: Boolean,
        api: Api
    ): ApiModel? {
        if (!(schema.type == "object" || schema.type == "array")) {
            return null
        }

        var apiModel: ApiModel?
        val modelName = if (isRequest) namingStrategy.createRequestModelName(
            httMethod, schema)
        else namingStrategy.createResponseModelName(schema)

        apiModel = if (isRequest) api.requestModels[modelName] else api.responseModels[modelName]

        if (apiModel != null) {
            return apiModel
        } else {
            apiModel = ApiModel()
        }

        apiModel.modelName = modelName

        val properties = schema.properties

        properties
            .forEach { (name, property) ->
                val isRequired = schema.requiredFields.contains(name)

                processProperty(
                    httMethod,
                    isRequest,
                    apiModel,
                    name,
                    property,
                    isRequired,
                    api
                )
        }

        if (isRequest) {
            api.requestModels[modelName] = apiModel
        } else {
            api.responseModels[modelName] = apiModel
        }

        return apiModel
    }

    private fun processProperty(
        httMethod: HttpMethod,
        isRequest: Boolean,
        apiModel: ApiModel,
        propertyName: String,
        propertySchema: Schema,
        isRequired: Boolean,
        api: Api) {
        var propertyType: Type = createType(propertySchema, httMethod, isRequest, api)

        if (httMethod == HttpMethod.PATCH && isRequest) {
            propertyType = DeclaredType("org.openapitools.jackson.nullable.JsonNullable",
                box(propertyType))
        }

        val schema = PropertySchema(
            format = propertySchema.format,
            required = isRequired,
            nullable = propertySchema.isNullable,
            readOnly = propertySchema.isReadOnly
        )

        if (propertySchema.type == "array") {
            val itemsSchema = processSchema(
                httMethod,
                propertySchema.itemsSchema,
                isRequest,
                api
            )
            schema.itemsSchema = itemsSchema
        }

        if (propertySchema.additionalPropertiesSchema != null &&
                propertySchema.additionalPropertiesSchema.getCreatingRef() != null) {
            processSchema(
                httMethod,
                propertySchema.additionalPropertiesSchema,
                isRequest,
                api
            )
        }

        if (propertySchema.additionalPropertiesSchema.type == "array") {
            processSchema(
                httMethod,
                propertySchema.additionalPropertiesSchema.itemsSchema,
                isRequest,
                api
            )
        }

        val apiModelProperty = ApiModelProperty(
            propertyType,
            propertySchema.format,
            isRequired,
            propertySchema.isNullable,
            propertySchema.isReadOnly,
            schema,
            httMethod == HttpMethod.PATCH
        )

        apiModel.addProperty(propertyName, apiModelProperty)
    }

    private fun box(type: Type): Type {
        return when (type.getKind()) {
            TypeKind.DOUBLE -> DeclaredType("java.lang.Double")
            TypeKind.BOOLEAN -> DeclaredType("java.lang.Boolean")
            TypeKind.SHORT -> DeclaredType("java.lang.Short")
            TypeKind.CHAR -> DeclaredType("java.lang.Character")
            TypeKind.INT -> DeclaredType("java.lang.Integer")
            TypeKind.LONG -> DeclaredType("java.lang.Long")
            TypeKind.FLOAT -> DeclaredType("java.lang.Float")
            else -> type
        }
    }

    private fun createType(
        schema: Schema,
        httMethod: HttpMethod,
        isRequest: Boolean,
        api: Api
    ): Type {
        val propertyType: Type

        if (typeConverter.isOpenApiType(schema)) {
            propertyType = typeConverter.convert(schema, isRequest)
        } else {
            propertyType = typeConverter.convert(schema, isRequest)

            /*
            val propertyTypeName = if (isRequest) namingStrategy.createRequestModelName(
                httMethod,
                schema
            ) else {
                if (schema.type == OpenApiType.ARRAY.type) {
                    namingStrategy.createResponseModelName(schema.itemsSchema)
                } else {
                    namingStrategy.createResponseModelName(schema)
                }
            }
             */

            /*
            val fullName: String

            if (propertyTypeName.contains(".")) {
                fullName = propertyTypeName
            } else {
                fullName = "${config.modelPackageName}.${propertyTypeName}"
            }

            propertyType = DeclaredType(fullName)
             */
        }

        if (schema.type == OpenApiType.ARRAY.type) {
            var elementType = createType(schema.itemsSchema, httMethod, isRequest, api)

            if (elementType.isPrimitive()) {
                elementType = typeConverter.unbox(elementType as PrimitiveType)
            }
            return DeclaredType("java.util.List", elementType)
        } else {
            return propertyType
        }
    }



}
