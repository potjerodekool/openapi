package org.platonos.rest.generate2.api

import org.platonos.rest.backend.Filer
import org.platonos.rest.generate.element.ElementKind
import org.platonos.rest.generate.element.PackageElement
import org.platonos.rest.generate.element.builder.Builders.method
import org.platonos.rest.generate.element.builder.Builders.typeElement
import org.platonos.rest.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.openapi.Types
import org.platonos.rest.openapi.api.*
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.Type
import org.platonos.rest.generate2.util.Functions.replaceFirstChar
import org.platonos.rest.generate2.ParametersResolver

class DelegateGenerator(delegateName: String,
                        val config: OpenApiGeneratorConfiguration,
                        val types: Types,
                        val filer: Filer) {

    private val typeElementBuilder = typeElement()
        .withEnclosingElement(PackageElement(config.apiPackageName))
        .withSimpleName(delegateName)
        .withKind(ElementKind.INTERFACE)

    fun finish() {
        filer.createSource(typeElementBuilder.build())
    }

    fun generate(url: String, path: ApiPath) {
        processPathOperation(HttpMethod.GET, url, path.get)
        processPathOperation(HttpMethod.POST, url, path.post)
        processPathOperation(HttpMethod.PUT, url, path.put)
        processPathOperation(HttpMethod.PATCH, url, path.patch)
        processPathOperation(HttpMethod.DELETE, url, path.delete)
    }

    private fun processPathOperation(method: HttpMethod, url: String, operation: ApiOperation?) {
        if (operation == null) {
            return
        }

        val methodName = generateMethodName(method, url, operation)

        val methodBuilder = method().withSimpleName(methodName)

        val parameters = ParametersResolver.resolveParameters(operation, config.modelPackageName)

        methodBuilder.withParameters(parameters)

        val responseCodeAndResponse = getResponse(operation)
        val response: ApiResponse?

        if (responseCodeAndResponse != null) {
            response = responseCodeAndResponse.second
        } else {
            response = null
        }

        var returnType = getResponseReturnType(
            method,
            response,
            operation.requestBody
        )

        if (has404Response(operation)) {
           returnType = DeclaredType("java.util.Optional", returnType)
        }

        methodBuilder.withReturnType(returnType)

        typeElementBuilder.withEnclosedElement(
            methodBuilder.build()
        )
    }

    private fun has404Response(operation: ApiOperation): Boolean {
        return operation.responses.containsKey("404")
    }

    private fun generateMethodName(method: HttpMethod,
                                   url: String,
                                   apiOperation: ApiOperation
    ): String {
        if (apiOperation.operationId != null) {
            return apiOperation.operationId
        }

        val methodName = StringBuilder()

        methodName.append(
            when(method) {
                HttpMethod.GET -> "get"
                HttpMethod.POST -> "post"
                HttpMethod.PUT -> "put"
                HttpMethod.PATCH -> "patch"
                HttpMethod.DELETE -> "delete"
            }
        )

        val elements = url.split("/")

        elements
            .filter { it.isNotEmpty() }
            .forEach { element ->
                methodName.append(element.replaceFirstChar { it.toUpperCase() })
            }

        return methodName.toString()
    }

    private fun getResponseReturnType(
        method: HttpMethod,
        response: ApiResponse?,
        requestBody: ApiRequestBody?
    ): Type {
        if (response == null) {
            val returnType = getReturnTypeFromRequestBody(method, requestBody)

            if (returnType != null) {
                return returnType
            }
            return types.getVoid()
        }

        val mediaType = response.contentMediaTypes[ContentType.APPLICATION_JSON.value]

        if (mediaType == null) {
            val returnType = getReturnTypeFromRequestBody(method, requestBody)

            if (returnType != null) {
                return returnType
            }
            return types.getVoid()
        }

        val model = mediaType.model
        val type = "${config.modelPackageName}.${model.modelName}"

        return DeclaredType(type)
    }

    private fun getReturnTypeFromRequestBody(method: HttpMethod,
                                             requestBody: ApiRequestBody?): Type? {
        if (method != HttpMethod.POST) {
            return null
        }

        if (requestBody == null) {
            return null
        }

        val mediaType = requestBody.contentMediaTypes[ContentType.APPLICATION_JSON.value]

        if (mediaType == null) {
            return null
        }

        val idProperty = mediaType.properties["id"]

        if (idProperty == null) {
            return null
        }

        return idProperty.propertyType
    }

    private fun getResponse(apiOperation: ApiOperation): Pair<String, ApiResponse>? {
        val responseCode = apiOperation.responses.keys.firstOrNull { is2xx(it) }

        if (responseCode == null) {
            return null
        }

        return responseCode to apiOperation.responses[responseCode]!!
    }

    private fun is2xx(code: String): Boolean {
        return code.length == 3 && code[0] == '2'
    }
}
