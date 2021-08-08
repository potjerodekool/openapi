package org.platonos.rest.gen.openapi.generator.api.spring

import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.doc.JavaDoc
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.builder.Builders.annotation
import org.platonos.rest.gen.element.builder.Builders.method
import org.platonos.rest.gen.element.builder.Builders.parameter
import org.platonos.rest.gen.element.builder.Builders.typeElement
import org.platonos.rest.gen.openapi.*
import org.platonos.rest.gen.openapi.api.ContentType
import org.platonos.rest.gen.openapi.generator.api.ApiGenerator
import org.platonos.rest.gen.openapi.generator.api.createJavaDoc
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.Type
import org.platonos.rest.gen.util.Functions.replaceFirstChar
import java.lang.StringBuilder

class ApiGeneratorSpring: ApiGenerator {

    private lateinit var modelNamingStrategy: ModelNamingStrategy
    private lateinit var typeConverter: TypeConverter

    private lateinit var apiName: String
    private lateinit var modelPackageName: String

    private val apiBuilder = typeElement()

    override fun init(
        config: OpenApiGeneratorConfiguration,
        options: Options,
        platformSupport: PlatformSupport,
        url: String,
        packageElement: PackageElement) {

        modelNamingStrategy = config.modelNamingStrategy
        typeConverter = platformSupport.getTypeConverter()


        apiName = createApiName(url)
        modelPackageName = options.modelPackageName

        apiBuilder.withKind(ElementKind.INTERFACE)
        apiBuilder.withSimpleName(apiName)
        apiBuilder.withEnclosingElement(packageElement)
    }

    override fun generate(url: String, path: Path) {
        if (path.post != null) {
            generatePostMethod(url, path.post)
        }

        if (path.get != null) {
            generateGetMethod(url, path.get)
        }

        if (path.put != null) {
            generatePutMethod(url, path.put)
        }

        if (path.patch != null) {
            generatePatchMethod(url, path.patch)
        }

        if (path.delete != null) {
            generateDeleteMethod(url, path.delete)
        }
    }

    override fun getTypeElement(): TypeElement {
        return apiBuilder.build()
    }

    private fun createApiName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() } + "Api"
    }

    private fun generateGetMethod(url: String, get: Operation) {
        val returnType = createReturnType(get, "200")
        val parameters = mutableListOf<VariableElement>()
        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.GetMapping"))
            .withValue(url)
            .build()

        get.parameters.forEach { uriParameter ->
            val name = uriParameter.name

            val parameterLocationType = when(ParameterLocation.fromLocation(uriParameter.`in`)) {
                ParameterLocation.PATH -> {
                    val pathVariableMissing = !(url.startsWith("{$name}") || url.contains("{$name}"))

                    if (pathVariableMissing) {
                        throw OpenApiException("url $url doens't contain pathvariable $name")
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

        val javaDoc = createJavaDoc(get)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withReturnType(returnType)
            .withSimpleName("get")
            .withParameters(parameters)
            .build()

        apiBuilder.withEnclosedElement(method)
    }

    private fun generatePostMethod(url: String, post: Operation) {
        val bodyParameter = createBodyParameter(post)
        val returnType = createReturnType(post, "201")

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.PostMapping"))
            .withValue(url)
            .build()

        val javaDoc = createJavaDoc(post)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withReturnType(returnType)
            .withSimpleName("create")

        if (bodyParameter != null) {
            method.withParameter(bodyParameter)
        }

        apiBuilder.withEnclosedElement(method.build())
    }

    private fun generatePutMethod(url: String, put: Operation) {
        val bodyRequired = put.requestBody.required

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.PutMapping"))
            .withValue(url)
            .build()

        val paramAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.RequestBody"),)

        if (bodyRequired) {
            paramAnnotation.withAttribute(Attribute.of("required", bodyRequired))
        }

        val bodyParameter = createBodyParameter(put)
        val returnType = createReturnType(put, "200")

        val javaDoc = createJavaDoc(put)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withReturnType(returnType)
            .withSimpleName("put")

        if (bodyParameter != null) {
            method.withParameter(bodyParameter)
        }

        apiBuilder.withEnclosedElement(method.build())
    }

    private fun generatePatchMethod(url: String, patch: Operation) {
        val returnType = createReturnType(patch, "200")
        val bodyParameter = createBodyParameter(patch, true)

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.PatchMapping"))
            .withValue(url)
            .build()

        val javaDoc = createJavaDoc(patch)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withReturnType(returnType)
            .withSimpleName("patch")

        if (bodyParameter != null) {
            method.withParameter(bodyParameter)
        }

        apiBuilder.withEnclosedElement(method.build())
    }

    private fun generateDeleteMethod(url: String, delete: Operation) {
        val returnType = createReturnType(delete, "200")
        val bodyParameter = createBodyParameter(delete)

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.DeleteMapping"))
            .withValue(url)
            .build()

        val javaDoc = createJavaDoc(delete)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withReturnType(returnType)
            .withSimpleName("delete")

        if (bodyParameter != null) {
            method.withParameter(bodyParameter)
        }

        apiBuilder.withEnclosedElement(method.build())
    }

    private fun createReturnType(operation: Operation, responseCode: String): Type {
        val contentMediaType = operation.responses[responseCode]!!.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)
        var returnType: Type

        if (contentMediaType == null) {
            returnType = DeclaredType("java.lang.Void")
        } else {
            val schema = contentMediaType.schema
            val modelSchema: Schema
            val isArray = schema.type == "array"

            if (isArray) {
                modelSchema = schema.itemsSchema
            } else {
                modelSchema = schema
            }

            if (modelSchema.getCreatingRef() != null) {
                val modelName = modelNamingStrategy.getModelName(modelSchema)!!
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
        }

        return DeclaredType("org.springframework.http.ResponseEntity", listOf(returnType))
    }

    private fun createBodyParameter(operation: Operation, isPatch: Boolean = false): VariableElement? {
        val requestBody = operation.requestBody

        if (requestBody == null) {
            return null
        }

        val bodyRequired = requestBody.required

        val paramAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.RequestBody"),)

        if (bodyRequired) {
            paramAnnotation.withAttribute(Attribute.of("required", bodyRequired))
        }

        val contentMediaType = operation.requestBody.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)
        val schema = contentMediaType.schema

        val modelName: String
        val bodyType: DeclaredType

        if (schema.getCreatingRef() != null) {
            modelName = modelNamingStrategy.getModelName(schema)!!
            val qualifiedModelName = "${modelPackageName}.$modelName"

            val bodyTypeName = if (isPatch) qualifiedModelName + "PatchRequest" else qualifiedModelName
            bodyType = DeclaredType(bodyTypeName)
        } else {
            modelName = "body"
            bodyType = DeclaredType(
                "java.util.Map",
                listOf(
                    DeclaredType("java.lang.String"),
                    DeclaredType("java.lang.Object")
                )
            )
        }

        val bodyParamName = modelName.lowercase()

        return parameter()
            .withAnnotation(paramAnnotation.build())
            .withModifier(Modifier.FINAL)
            .withType(bodyType)
            .withSimpleName(bodyParamName)
            .build()
    }

}