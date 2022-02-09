package org.platonos.rest.generate2.api.spring

import org.platonos.rest.backend.Filer
import org.platonos.rest.generate.doc.JavaDoc
import org.platonos.rest.generate.element.*
import org.platonos.rest.generate.element.Annotation
import org.platonos.rest.generate.element.builder.Builders
import org.platonos.rest.generate.element.builder.MethodBuilder
import org.platonos.rest.generate.element.builder.TypeElementBuilder
import org.platonos.rest.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.openapi.Types
import org.platonos.rest.openapi.api.*
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.Type
import org.platonos.rest.generate2.util.Functions.replaceFirstChar
import org.platonos.rest.generate2.ParametersResolver
import org.platonos.rest.generate2.api.ApiGenerator

abstract class AbstractApiGeneratorSpring(
    private val controllerName: String,
    val config: OpenApiGeneratorConfiguration,
    val types: Types,
    val filer: Filer) : ApiGenerator {

    companion object {
        private const val VOID = "java.lang.Void"
    }

    private val typeElementBuilder = Builders.typeElement()

    override fun init() {
        val apiPackageName = config.apiPackageName
        typeElementBuilder.enclosingElement = PackageElement(apiPackageName)
        initTypeElement(typeElementBuilder, controllerName)
    }

    protected abstract fun initTypeElement(typeElementBuilder: TypeElementBuilder,
                                           controllerName: String)

    override fun generate(url: String, path: ApiPath) {
        processPathOperation(HttpMethod.GET, url, path.get)
        processPathOperation(HttpMethod.POST, url, path.post)
        processPathOperation(HttpMethod.PUT, url, path.put)
        processPathOperation(HttpMethod.PATCH, url, path.patch)
        processPathOperation(HttpMethod.DELETE, url, path.delete)
    }

    private fun processPathOperation(httpMethod: HttpMethod,
                                     url: String,
                                     apiOperation: ApiOperation?) {
        if (apiOperation == null) {
            return
        }

        val methodName = generateMethodName(httpMethod, url, apiOperation)

        val responseCodeAndResponse = getResponse(apiOperation)
        val responseCode: String?
        val response: ApiResponse?

        if (responseCodeAndResponse != null) {
            responseCode = responseCodeAndResponse.first
            response = responseCodeAndResponse.second
        } else {
            responseCode = null
            response = null
        }

        val returnTypeTypeArg = getResponseReturnType(response)

        val returnType = DeclaredType("org.springframework.http.ResponseEntity", returnTypeTypeArg)

        val methodBuilder = Builders.method()
            .withSimpleName(methodName)
            .withReturnType(returnType)

        addJavaDoc(httpMethod, url, apiOperation, responseCode, response, methodBuilder)
        addMethodParameters(apiOperation, methodBuilder)
        addMethodAnnotations(methodBuilder, httpMethod, url, apiOperation)
        addMethodBody(httpMethod, url, apiOperation, methodBuilder)
        adjustMethod(methodBuilder)

        typeElementBuilder.withEnclosedElement(methodBuilder.build())
    }

    open fun adjustMethod(methodBuilder: MethodBuilder) {
    }

    protected abstract fun addMethodBody(httpMethod: HttpMethod,
                                         url: String,
                                         apiOperation: ApiOperation,
                                         methodBuilder: MethodBuilder)

    private fun addJavaDoc(httpMethod: HttpMethod,
                           url: String,
                           apiOperation: ApiOperation,
                           responseCode: String?,
                           response: ApiResponse?,
                           methodBuilder: MethodBuilder
    ) {
        val responseDescription =
            if (response != null) "${response.description} (status code $responseCode)"
            else ""

        val javaDoc = JavaDoc("""
            /**
                ${httpMethod.name} $url: ${appendTexts(apiOperation.summary, apiOperation.description)}
                
                @return $responseDescription
            */
        """.trimIndent())

        methodBuilder.withJavaDoc(javaDoc)
    }

    private fun addMethodParameters(apiOperation: ApiOperation,
                                    methodBuilder: MethodBuilder) {
        val parameters = adjustParameters(
            ParametersResolver.resolveParameters(
                apiOperation,
                config.modelPackageName
            )
        )

        methodBuilder.withParameters(parameters)
    }

    open fun adjustParameters(parameters: List<VariableElement>): List<VariableElement> {
        return parameters
    }

    private fun addMethodAnnotations(methodBuilder: MethodBuilder,
                                     httpMethod: HttpMethod,
                                     url: String,
                                     apiOperation: ApiOperation
    ) {

        val tags = apiOperation.tags
            .map { ConstantAttributeValue(it) }

        val apiOperationAnnotation = Builders.annotation()
            .withType("io.swagger.annotations.ApiOperation")
            .withAttribute("value", apiOperation.summary ?: "")
            .withAttribute("nickname", methodBuilder.simpleName)
            .withAttribute("notes", apiOperation.description ?: "")
            .withAttribute("tags", ArrayAttributeValue(tags))
            .build()

        methodBuilder.withAnnotation(apiOperationAnnotation)
        methodBuilder.withAnnotation(createApiResponsesAnnotation(apiOperation))
        methodBuilder.withAnnotation(createRequestMappingAnnotation(httpMethod, url, apiOperation))
    }

    private fun createApiResponsesAnnotation(apiOperation: ApiOperation): Annotation {
        val annotationBuilder = Builders.annotation()
            .withType("io.swagger.annotations.ApiResponses")

        val apiResponses = mutableListOf<AnnotationAttributeValue>()

        apiOperation.responses.forEach { (code, response) ->
            val contentMediaType = response.contentMediaTypes[ContentType.APPLICATION_JSON.value]

            val fullModelName: String? =
                if (contentMediaType != null) {
                    val modelName = contentMediaType.model.modelName
                    "${config.modelPackageName}.${modelName}"
                } else {
                    null
                }

            val apiResponseAnnotationBuilder = Builders.annotation()
                .withType("io.swagger.annotations.ApiResponse")
                .withAttribute("code", code.toInt())
                .withAttribute("message", response.description)

            if (fullModelName != null) {
                apiResponseAnnotationBuilder
                    .withAttribute("response", ClassAttributeValue(DeclaredType(fullModelName)))
            }

            apiResponses.add(AnnotationAttributeValue(apiResponseAnnotationBuilder.build()))
        }

        annotationBuilder.withAttribute("value", ArrayAttributeValue(apiResponses))
        return annotationBuilder.build()
    }

    private fun createRequestMappingAnnotation(httpMethod: HttpMethod,
                                               url: String,
                                               apiOperation: ApiOperation
    ): Annotation {
        val annotationBuilder = Builders.annotation()

        val annotationType = when(httpMethod) {
            HttpMethod.GET -> "org.springframework.web.bind.annotation.GetMapping"
            HttpMethod.POST -> "org.springframework.web.bind.annotation.PostMapping"
            HttpMethod.PUT -> "org.springframework.web.bind.annotation.PutMapping"
            HttpMethod.PATCH -> "org.springframework.web.bind.annotation.PatchMapping"
            HttpMethod.DELETE -> "org.springframework.web.bind.annotation.DeleteMapping"
        }

        annotationBuilder.withType(annotationType)
        annotationBuilder.withAttribute("value", url)

        if (apiOperation.requestBody != null) {
            val requestBody = apiOperation.requestBody
            val contentTypes = requestBody.contentMediaTypes.keys.toTypedArray()

            if (contentTypes.size > 0) {
                annotationBuilder.withAttribute("consumes", contentTypes)
            }
        }

        val produces = apiOperation.responses.values
            .flatMap { it.contentMediaTypes.keys }

        if (produces.size > 0) {
            annotationBuilder.withAttribute("produces", produces)
        }
        return annotationBuilder.build()
    }

    private fun appendTexts(first: String?, second: String?): String {
        val text = StringBuilder()

        if (first != null) {
            text.append(addDotIfNeeded(first))

            if (text.endsWith(" ").not()) {
                text.append(" ")
            }
        }

        if (second != null) {
            text.append(addDotIfNeeded(second))
        }

        return text.toString()
    }

    private fun addDotIfNeeded(text: String):String {
        return if (text.trim().endsWith(".")) text else "$text."
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

    private fun getResponse(apiOperation: ApiOperation): Pair<String, ApiResponse>? {
        val responseCode = apiOperation.responses.keys.firstOrNull { is2xx(it) }

        if (responseCode == null) {
            return null
        }

        return responseCode to apiOperation.responses[responseCode]!!
    }

    private fun getResponseReturnType(response: ApiResponse?): Type {
        if (response == null) {
            return types.getDeclaredType(VOID)
        }

        val mediaType = response.contentMediaTypes[ContentType.APPLICATION_JSON.value]

        if (mediaType == null) {
            return types.getDeclaredType(VOID)
        }

        val model = mediaType.model
        val type = "${config.apiPackageName}.${model.modelName}"

        return DeclaredType(type)
    }

    fun is2xx(code: String): Boolean {
        return code.length == 3 && code[0] == '2'
    }

    override fun finish() {
        filer.createSource(typeElementBuilder.build())
    }
}