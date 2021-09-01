package org.platonos.rest.generate.openapi.generator.api.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.element.*
import org.platonos.rest.generate.element.Annotation
import org.platonos.rest.generate.element.builder.AnnotationBuilder
import org.platonos.rest.generate.element.builder.Builders
import org.platonos.rest.generate.element.builder.Builders.annotation
import org.platonos.rest.generate.element.builder.Builders.parameter
import org.platonos.rest.generate.element.builder.MethodBuilder
import org.platonos.rest.generate.openapi.*
import org.platonos.rest.generate.openapi.api.ContentType
import org.platonos.rest.generate.openapi.api.HttpMethod
import org.platonos.rest.generate.openapi.generator.Filer
import org.platonos.rest.generate.openapi.generator.api.ApiGenerator
import org.platonos.rest.generate.openapi.generator.api.createJavaDoc
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.Type

abstract class AbstractSpringApiGenerator : ApiGenerator {

    protected lateinit var modelNamingStrategy: ModelNamingStrategy
    protected lateinit var typeConverter: TypeConverter
    protected lateinit var modelPackageName: String
    protected lateinit var apiPackage: String
    private val dynamicModels = mutableListOf<String>()
    protected val apiBuilder = Builders.typeElement()
    protected lateinit var filer: Filer

    private val objectMapper = ObjectMapper()

    override fun init(config: OpenApiGeneratorConfiguration,
                      platformSupport: PlatformSupport,
                      url: String,
                      packageElement: PackageElement,
                      filer: Filer) {
        modelNamingStrategy = config.modelNamingStrategy
        typeConverter = platformSupport.getTypeConverter()
        modelPackageName = config.modelPackageName
        apiPackage = config.apiPackageName
        dynamicModels.addAll(config.dynamicModels)
        this.filer = filer

        val apiName = createClassName(url)

        apiBuilder.withKind(getElementKind())
        apiBuilder.withSimpleName(apiName)
        apiBuilder.withEnclosingElement(packageElement)
    }

    fun generate(url: String, path: Path) {
        if (path.post != null) {
            generateOperationMethod(HttpMethod.POST, url, path.post)
        }

        if (path.get != null) {
            generateOperationMethod(HttpMethod.GET, url, path.get)
        }

        if (path.put != null) {
            generateOperationMethod(HttpMethod.PUT, url, path.put)
        }

        if (path.patch != null) {
            generateOperationMethod(HttpMethod.PATCH, url, path.patch)
        }

        if (path.delete != null) {
            generateOperationMethod(HttpMethod.DELETE, url, path.delete)
        }
    }

    private fun generateOperationMethod(httpMethod: HttpMethod,
                                        url: String,
                                        operation: Operation
    ) {
        val returnType = createReturnType(operation)

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType(resolveRequestMappingAnnotationName(httpMethod)))
            .withValue(url)
            .apply {
                addConsumesAttribute(operation, this)
                addProducesAttribute(operation, this)
            }.build()

        val javaDoc = createJavaDoc(httpMethod, url, operation)

        val methodName = createMethodName(httpMethod, operation)

        val methodBuilder = Builders.method()
            .withJavaDoc(javaDoc)
            .withAnnotation(createApiOperationAnnotation(operation))
            .withAnnotation(createApiResponsesAnnotation(operation))
            .withAnnotation(requestMappingAnnotation)
            .withReturnType(returnType)
            .withSimpleName(methodName)
            .withParameters(createMethodParameters(
                httpMethod,
                url,
                operation
            ))


        buildMethod(httpMethod, url, operation, methodBuilder)

        apiBuilder.withEnclosedElement(methodBuilder.build())
    }

    fun createMethodParameters(httpMethod: HttpMethod,
                               url: String,
                               operation: Operation): MutableList<VariableElement> {
        val parameters = mutableListOf<VariableElement>()
        parameters += createPathVariableParams(url, operation)

        val bodyParameter = createBodyParameter(httpMethod, operation)
        if (bodyParameter != null) {
            parameters += bodyParameter
        }

        parameters += createHttpServletRequestParameter()
        return parameters
    }

    abstract fun buildMethod(httpMethod: HttpMethod,
                             url: String,
                             operation: Operation,
                             methodBuilder: MethodBuilder)

    fun createReturnType(operation: Operation): Type {
        var returnType: Type

        val response = operation.responses.entries
            .filter { is2XX(it.key) }
            .map { it.value }
            .firstOrNull()

        if (response != null) {
            val contentMediaType = response.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)

            if (contentMediaType != null) {
                val schema = contentMediaType.schema
                val isArray = schema.type == "array"
                val modelSchema: Schema = if (isArray) schema.itemsSchema else schema

                if (modelSchema.getCreatingRef() != null) {
                    val modelName = modelNamingStrategy.createModelName(modelSchema)
                    val qualifiedModelName = "${modelPackageName}.$modelName"
                    returnType = DeclaredType(qualifiedModelName)
                } else {
                    returnType = DeclaredType(
                        "java.util.Map",
                        listOf(
                            DeclaredType("java.util.String"),
                            DeclaredType("java.util.Object")
                        )
                    )
                }

                if (isArray) {
                    returnType = DeclaredType("java.util.List", listOf(returnType))
                }
            } else {
                returnType = DeclaredType("java.lang.Void")
            }
        } else {
            returnType = DeclaredType("java.lang.Void")
        }

        return DeclaredType("org.springframework.http.ResponseEntity", listOf(returnType))
    }

    private fun resolveRequestMappingAnnotationName(httpMethod: HttpMethod): String {
        return when(httpMethod) {
            HttpMethod.POST -> "org.springframework.web.bind.annotation.PostMapping"
            HttpMethod.GET -> "org.springframework.web.bind.annotation.GetMapping"
            HttpMethod.PUT -> "org.springframework.web.bind.annotation.PutMapping"
            HttpMethod.PATCH -> "org.springframework.web.bind.annotation.PatchMapping"
            HttpMethod.DELETE -> "org.springframework.web.bind.annotation.DeleteMapping"
        }
    }

    fun is2XX(httpCode: String): Boolean {
        return httpCode.startsWith("2")
    }

    private fun addConsumesAttribute(operation: Operation,
                                     annotationBuilder: AnnotationBuilder
    ) {
        val contentMediaTypes = operation.requestBody.contentMediaTypes.keys
            .toList()

        if (contentMediaTypes.isNotEmpty()) {
            annotationBuilder.withAttribute(Attribute.of("consumes", contentMediaTypes))
        }
    }

    private fun addProducesAttribute(operation: Operation,
                                     annotationBuilder: AnnotationBuilder
    ) {
        if (operation.responses != null) {
            val contentMediaTypes = operation.responses
                .map { response -> response.value.contentMediaTypes }
                .map { contentMediaTypes -> contentMediaTypes.keys }
                .flatten()
                .distinct()

            if (contentMediaTypes.isNotEmpty()) {
                annotationBuilder.withAttribute(Attribute.of("produces", contentMediaTypes))
            }
        }
    }

    fun createMethodName(
        httpMethod: HttpMethod,
        operation: Operation
    ): String {
        if (operation.operationId != null) {
            return operation.operationId
        }

        return when(httpMethod) {
            HttpMethod.POST -> {
                if (operation.responses.containsKey("201")) {
                    "create"
                } else {
                    "post"
                }
            }
            HttpMethod.GET -> "get"
            HttpMethod.PUT -> "put"
            HttpMethod.PATCH -> "patch"
            HttpMethod.DELETE -> "delete"
        }
    }

    private fun createApiOperationAnnotation(operation: Operation): Annotation {
        val operationAnnotation = annotation()
            .withType(DeclaredType("io.swagger.annotations.ApiOperation"))

        operationAnnotation.withAttribute(Attribute.of("value", operation.summary ?: ""))

        if (operation.operationId != null) {
            operationAnnotation.withAttribute(Attribute.of("nickname", operation.operationId))
        }

        if (operation.description != null) {
            operationAnnotation.withAttribute(Attribute.of("notes", operation.description))
        }

        operationAnnotation.withAttribute(Attribute.of("tags", ArrayAttributeValue()))

        return operationAnnotation.build()
    }

    private fun createApiResponsesAnnotation(operation: Operation): Annotation {
        val responseAnnotations = operation.responses.map { (statusCode, response) ->
            val responseAnnotation = annotation()
                .withType(DeclaredType("io.swagger.annotations.ApiResponse"))
                .withAttribute(Attribute.of("code", statusCode.toInt()))
                .withAttribute(Attribute.of("message", response.description ?: ""))

            val contentType = response.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)

            if (contentType != null) {
                var responseSchema = contentType.schema
                var isArray = false

                if (responseSchema.type == "array") {
                    responseSchema = responseSchema.itemsSchema
                    isArray = true
                }

                val modelName = modelNamingStrategy.createModelName(responseSchema)
                val fullModelName: String =
                    if (responseSchema.getCreatingRef() != null) {
                        "${modelPackageName}.$modelName"
                    } else {
                        modelName
                    }

                responseAnnotation.withAttribute(
                    Attribute.of("response",
                        ClassAttributeValue(DeclaredType(fullModelName))
                    )
                )

                if (isArray) {
                    responseAnnotation.withAttribute(Attribute.of("responseContainer", "List"))
                }
            }

            AnnotationAttributeValue(responseAnnotation.build())
        }

        return annotation()
            .withType(DeclaredType("io.swagger.annotations.ApiResponses"))
            .withAttribute(Attribute.of("value", ArrayAttributeValue(responseAnnotations)))
            .build()
    }

    private fun createPathVariableParams(url: String,
                                 operation: Operation): List<VariableElement> {
        val parameters = mutableListOf<VariableElement>()

        operation.parameters.forEach { uriParameter ->
            val name = uriParameter.name

            val parameterLocationType = when(ParameterLocation.fromLocation(uriParameter.`in`)) {
                ParameterLocation.PATH -> {
                    val pathVariableMissing = !(url.startsWith("{$name}") || url.contains("{$name}"))

                    if (pathVariableMissing) {
                        throw OpenApiException("url $url doesn't contain path variable $name")
                    }
                    DeclaredType("org.springframework.web.bind.annotation.PathVariable")
                }
                ParameterLocation.QUERY -> {
                    DeclaredType("org.springframework.web.bind.annotation.RequestParam")
                }
                ParameterLocation.HEADER -> {
                    DeclaredType("org.springframework.web.bind.annotation.RequestHeader")
                }
                ParameterLocation.COOKIE -> {
                    DeclaredType("org.springframework.web.bind.annotation.CookieValue")
                }
            }

            val required = uriParameter.required

            val annotations = mutableListOf<Annotation>()

            annotations.add(
                annotation()
                    .withType(parameterLocationType)
                    .withAttribute(Attribute.of("name", name))
                    .withAttribute(Attribute.of("required", required))
                    .build()
            )

            if (uriParameter.isDeprecated) {
                annotations.add(
                    annotation()
                        .withType(DeclaredType("java.lang.Deprecated"))
                        .build()
                )
            }

            val parameterType = typeConverter.convert(uriParameter.schema.type, uriParameter.schema)

            parameters.add(
                parameter()
                    .withAnnotations(annotations)
                    .withModifier(Modifier.FINAL)
                    .withType(parameterType)
                    .withSimpleName(name)
                    .build()
            )
        }

        return parameters
    }

    private fun createBodyParameter(httpMethod: HttpMethod,
                                    operation: Operation): VariableElement? {
        val requestBody = operation.requestBody
        val bodyRequired = requestBody.isRequired

        val paramAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.RequestBody"))

        if (bodyRequired) {
            paramAnnotation.withAttribute(Attribute.of("required", bodyRequired))
        }

        val schema = getRequestBodySchema(operation)

        if (schema == null) {
            return null
        }

        val modelName = "body"
        val bodyType: DeclaredType
        var isDynamicModel = false

        if (schema.getCreatingRef() != null) {
            val modelClassName = if (httpMethod == HttpMethod.PATCH) modelNamingStrategy.createPatchModelName(schema) else
                modelNamingStrategy.createModelName(schema)

            if (dynamicModels.contains(modelClassName).not()) {
                val qualifiedModelName = "${modelPackageName}.$modelClassName"
                bodyType = DeclaredType(qualifiedModelName)
            } else {
                bodyType = DeclaredType(
                    "java.util.Map",
                    listOf(
                        DeclaredType("java.lang.String"),
                        DeclaredType("java.lang.Object")
                    )
                )
                isDynamicModel = true
            }
        } else {
            bodyType = DeclaredType(
                "java.util.Map",
                listOf(
                    DeclaredType("java.lang.String"),
                    DeclaredType("java.lang.Object")
                )
            )
        }

        val validAnnotation = annotation()
            .withType(DeclaredType("javax.validation.Valid"))
            .build()

        val parameterBuilder = parameter()

        if (isDynamicModel) {
            val jsonExample = createJsonExample(schema)

            val exampleObjectAnnotion = annotation().withType(DeclaredType("io.swagger.v3.oas.annotations.media.ExampleObject"))
                .withAttribute(Attribute.of(
                    "value",
                    jsonExample
                )).build()

            val contentAnnotation = annotation()
                .withType(DeclaredType("io.swagger.v3.oas.annotations.media.Content"))
                .withAttribute(Attribute.of("examples", AnnotationAttributeValue(exampleObjectAnnotion)))
                .build()

            parameterBuilder.withAnnotation(
                annotation()
                    .withType(DeclaredType("io.swagger.v3.oas.annotations.parameters.RequestBody"))
                    .withAttribute(Attribute.of(
                        "content",
                        AnnotationAttributeValue(contentAnnotation)
                    )).build()
            )
        }

        return parameterBuilder
            .withAnnotation(paramAnnotation.build())
            .withAnnotation(validAnnotation)
            .withType(bodyType)
            .withSimpleName(modelName)
            .build()
    }

    private fun createJsonExample(schema: Schema): String {
        return schema.properties.map { (name, propertySchema) ->
            val value = createPropertyValue(propertySchema)
            "\\\"$name\\\"=$value"
        }.joinToString(
            prefix = "{",
            separator = ",",
            postfix = "}"
        )
    }

    private fun createPropertyValue(propertySchema: Schema): Any {
        val openApiType = OpenApiType.fromType(propertySchema.type)
        val format = propertySchema.format

        return when(openApiType) {
            OpenApiType.STRING -> {
                if (format == null) {
                    "\\\"test\\\""
                } else if (format == "date") {
                    return "\\\"1970-01-01\\\""
                } else {
                    TODO()
                }
            }
            else -> TODO()
        }
    }

    private fun createHttpServletRequestParameter(): VariableElement {
        return parameter()
            .withModifier(Modifier.FINAL)
            .withType(DeclaredType("javax.servlet.http.HttpServletRequest"))
            .withSimpleName("httpServletRequest")
            .build()
    }

    private fun getRequestBodySchema(operation: Operation): Schema? {
        val contentMediaType = operation.requestBody.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)
        return contentMediaType?.schema
    }

    abstract fun createClassName(url: String): String

    abstract fun getElementKind(): ElementKind
}