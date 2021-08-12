package org.platonos.rest.gen.openapi.generator.api.spring

import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.builder.Builders
import org.platonos.rest.gen.element.builder.Builders.annotation
import org.platonos.rest.gen.element.builder.Builders.method
import org.platonos.rest.gen.element.builder.Builders.methodInvocation
import org.platonos.rest.gen.element.builder.Builders.parameter
import org.platonos.rest.gen.expression.Expression
import org.platonos.rest.gen.expression.FieldAccess
import org.platonos.rest.gen.expression.IdentifierExpression
import org.platonos.rest.gen.openapi.*
import org.platonos.rest.gen.openapi.api.ContentType
import org.platonos.rest.gen.openapi.generator.api.ApiDefinitionGenerator
import org.platonos.rest.gen.openapi.generator.api.createJavaDoc
import org.platonos.rest.gen.statement.ReturnStatement
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.Type

class ApiDefinitionGeneratorSpring : ApiDefinitionGenerator, AbstractGenerator() {

    private val apiBuilder = Builders.typeElement()

    override fun init(config: OpenApiGeneratorConfiguration,
             platformSupport: PlatformSupport,
             url: String,
             packageElement: PackageElement) {
        super.init(config, platformSupport, url, packageElement)

        val apiName = createApiName(url)

        apiBuilder.withKind(ElementKind.INTERFACE)
        apiBuilder.withSimpleName(apiName)
        apiBuilder.withEnclosingElement(packageElement)
    }

    override fun generateApiDefinition(url: String, path: Path) {
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

    override fun getApiDefinition(): TypeElement {
        return apiBuilder.build()
    }

    private fun generatePostMethod(url: String, post: Operation) {
        val returnType = createReturnType(post, "201")

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.PostMapping"))
            .withValue(url)
            .withAttribute(createConsumesAttribute(post))
            .withAttribute(createProducesAttribute(post))
            .build()

        val javaDoc = createJavaDoc(post)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withAnnotation(createOperationAnnotation(post))
            .withModifier(Modifier.DEFAULT)
            .withReturnType(returnType)
            .withSimpleName("create")
            .withParameter(createBodyParameter(post).build())
            .withParameter(createHttpServletRequestParameter().build())
            .withBody(ReturnStatement(responseEntityNotFound()))

        apiBuilder.withEnclosedElement(method.build())
    }

    private fun generateGetMethod(url: String, get: Operation) {
        val returnType = createReturnType(get, "200")
        val parameters = mutableListOf<VariableElement>()
        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.GetMapping"))
            .withValue(url)
            .withAttribute(createConsumesAttribute(get))
            .withAttribute(createProducesAttribute(get))
            .build()

        get.parameters.forEach { uriParameter ->
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

        parameters.add(createHttpServletRequestParameter().build())

        val javaDoc = createJavaDoc(get)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withAnnotation(createOperationAnnotation(get))
            .withModifier(Modifier.DEFAULT)
            .withReturnType(returnType)
            .withSimpleName("get")
            .withParameters(parameters)
            .withBody(ReturnStatement(responseEntityNotFound()))
            .build()

        apiBuilder.withEnclosedElement(method)
    }

    private fun generatePutMethod(url: String, put: Operation) {
        val bodyRequired = put.requestBody.required

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.PutMapping"))
            .withValue(url)
            .withAttribute(createConsumesAttribute(put))
            .withAttribute(createProducesAttribute(put))
            .build()

        val paramAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.RequestBody"))

        if (bodyRequired) {
            paramAnnotation.withAttribute(Attribute.of("required", bodyRequired))
        }

        val returnType = createReturnType(put, "200")

        val javaDoc = createJavaDoc(put)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withAnnotation(createOperationAnnotation(put))
            .withModifier(Modifier.DEFAULT)
            .withReturnType(returnType)
            .withSimpleName("put")
            .withParameter(createBodyParameter(put).build())
            .withParameter(createHttpServletRequestParameter().build())
            .withBody(ReturnStatement(responseEntityNotFound()))
            .build()

        apiBuilder.withEnclosedElement(method)
    }

    private fun generatePatchMethod(url: String, patch: Operation) {
        val returnType = createReturnType(patch, "200")

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.PatchMapping"))
            .withValue(url)
            .withAttribute(createConsumesAttribute(patch))
            .withAttribute(createProducesAttribute(patch))
            .build()

        val javaDoc = createJavaDoc(patch)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withAnnotation(createOperationAnnotation(patch))
            .withModifier(Modifier.DEFAULT)
            .withReturnType(returnType)
            .withSimpleName("patch")
            .withParameter(createBodyParameter(patch, true).build())
            .withParameter(createHttpServletRequestParameter().build())
            .withBody(ReturnStatement(responseEntityNotFound()))
            .build()

        apiBuilder.withEnclosedElement(method)
    }

    private fun generateDeleteMethod(url: String, delete: Operation) {
        val returnType = createReturnType(delete, "200")

        val requestMappingAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.DeleteMapping"))
            .withValue(url)
            .withAttribute(createConsumesAttribute(delete))
            .withAttribute(createProducesAttribute(delete))
            .build()

        val javaDoc = createJavaDoc(delete)

        val method = method()
            .withJavaDoc(javaDoc)
            .withAnnotation(requestMappingAnnotation)
            .withAnnotation(createOperationAnnotation(delete))
            .withModifier(Modifier.DEFAULT)
            .withReturnType(returnType)
            .withSimpleName("delete")

        if (delete.requestBody != null) {
            method.withParameter(createBodyParameter(delete).build())
        }

        method.withParameter(createHttpServletRequestParameter().build())
        method.withBody(ReturnStatement(responseEntityNotFound()))

        apiBuilder.withEnclosedElement(method.build())
    }

    private fun createReturnType(operation: Operation, responseCode: String): Type {
        val contentMediaType = operation.responses[responseCode]!!.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)
        var returnType: Type

        if (contentMediaType == null) {
            returnType = DeclaredType("java.lang.Void")
        } else {
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
        }

        return DeclaredType("org.springframework.http.ResponseEntity", listOf(returnType))
    }

    private fun responseEntityNotFound(): Expression {
        val notFoundInvocation = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression("org.springframework.http.ResponseEntity", DeclaredType("org.springframework.http.ResponseEntity")),
                    IdentifierExpression("notFound")
                )
            ).build()

        return methodInvocation()
            .withSelect(
                FieldAccess(
                    notFoundInvocation,
                    IdentifierExpression("build")
                )
            ).build()
    }

    private fun createConsumesAttribute(operation: Operation): Attribute {
        val contentMediaTypes = operation.requestBody.contentMediaTypes.keys
            .toList()
        return Attribute.of("consumes", contentMediaTypes)
    }

    private fun createProducesAttribute(operation: Operation): Attribute {
        if (operation.responses != null) {
            val contentMediaTypes = operation.responses
                .map { response -> response.value.contentMediaTypes }
                .map { contentMediaTypes -> contentMediaTypes.keys }
                .flatten()
                .distinct()

            return Attribute.of("produces", contentMediaTypes)
        }

        return Attribute.of("produces", emptyList<String>())
    }

    private fun createOperationAnnotation(operation: Operation): Annotation {
        val operationAnnotation = annotation()
            .withType(DeclaredType("io.swagger.v3.oas.annotations.Operation"))

        if (operation.operationId != null) {
            operationAnnotation.withAttribute(Attribute.of("operationId", operation.operationId))
        }

        return operationAnnotation.build()
    }

}