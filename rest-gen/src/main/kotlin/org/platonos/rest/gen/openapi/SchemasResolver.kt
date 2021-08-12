package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.model3.*
import org.platonos.rest.gen.openapi.api.HttpMethod
import org.platonos.rest.gen.openapi.api.ContentType

class SchemasResolver(config: OpenApiGeneratorConfiguration) : OpenApiVisitor {

    private val schemas = mutableMapOf<String, Schema>()
    private val patchSchemas = mutableMapOf<String, Schema>()

    private val modelNamingStrategy = config.modelNamingStrategy

    fun getSchemas(): Map<String, Schema> {
        return schemas
    }

    fun getPatchSchemas(): Map<String, Schema> {
        return patchSchemas
    }

    override fun visitRequestBody(method: HttpMethod, operation: Operation, requestBody: RequestBody) {
        super.visitRequestBody(method, operation, requestBody)

        val contentContentType = requestBody.contentMediaTypes[ContentType.APPLICATION_JSON.descriptor]

        if (contentContentType != null) {
            val schema = contentContentType.schema

            if (schema != null) {
                val modelName = visitSchemaAndProperties(schema)

                if (modelName != null && method === HttpMethod.PATCH) {
                    patchSchemas[modelName] = schema
                }
            }
        }
    }

    override fun visitResponse(method: HttpMethod, operation: Operation, responseCode: String, response: Response) {
        super.visitResponse(method, operation, responseCode, response)

        val contentContentType = response.contentMediaTypes[ContentType.APPLICATION_JSON.descriptor]

        if (contentContentType != null) {
            val schema = contentContentType.schema

            if (schema != null) {
                visitSchemaAndProperties(schema)
            }
        }
    }

    private fun visitSchemaAndProperties(schema: Schema): String? {
        if (schema.type != "object") {
            return null
        }

        val modelName = modelNamingStrategy.createModelName(schema)

        if (schemas.containsKey(modelName).not()) {
            schemas[modelName] = schema

            schema.properties.values.forEach { propertySchema ->
                visitSchemaAndProperties(propertySchema)
            }
        }

        return modelName
    }
}