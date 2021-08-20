package org.platonos.rest.gen.openapi.generator.api.spring

import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.element.*
import org.platonos.rest.gen.element.Annotation
import org.platonos.rest.gen.element.builder.Builders.annotation
import org.platonos.rest.gen.element.builder.Builders.field
import org.platonos.rest.gen.element.builder.Builders.method
import org.platonos.rest.gen.element.builder.Builders.methodInvocation
import org.platonos.rest.gen.element.builder.Builders.parameter
import org.platonos.rest.gen.element.builder.MethodBuilder
import org.platonos.rest.gen.expression.*
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.openapi.api.ContentType
import org.platonos.rest.gen.openapi.api.HttpMethod
import org.platonos.rest.gen.openapi.generator.Filer
import org.platonos.rest.gen.openapi.generator.api.ApiImplementationGenerator
import org.platonos.rest.gen.openapi.resolver.IdProperty
import org.platonos.rest.gen.statement.BlockStatement
import org.platonos.rest.gen.statement.ExpressionStatement
import org.platonos.rest.gen.statement.ReturnStatement
import org.platonos.rest.gen.statement.Statement
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.PrimitiveType
import org.platonos.rest.gen.type.Type
import org.platonos.rest.gen.util.Functions.replaceFirstChar

class ApiImplementationGeneratorSpring : ApiImplementationGenerator, AbstractSpringApiGenerator() {

    private lateinit var delegateGenerator: DelegateGeneratorSpring

    private lateinit var idSchemas: Map<String, IdProperty>

    override fun init(
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        url: String,
        packageElement: PackageElement,
        filer: Filer,
        idSchemas: Map<String, IdProperty>) {
        super.init(config, platformSupport, url, packageElement, filer)

        this.idSchemas = idSchemas

        val packageName = packageElement.getQualifiedName()

        val controllerName = createControllerName(url)
        val delegateSimpleName = createDelegateName(url)
        val delegateName = "${packageName}.$delegateSimpleName"

        delegateGenerator = DelegateGeneratorSpring(packageElement, delegateSimpleName)

        apiBuilder.withAnnotation(
            annotation()
                .withType(DeclaredType("io.swagger.annotations.Api"))
                .build()
        )
        apiBuilder.withAnnotation(
            annotation()
                .withType(DeclaredType("org.springframework.web.bind.annotation.RestController"))
                .build()
        )

        apiBuilder.withEnclosedElement(
            field()
                .withModifier(Modifier.PRIVATE)
                .withModifier(Modifier.FINAL)
                .withType(DeclaredType(delegateName))
                .withSimpleName("delegate")
                .build()
        )

        val constructorBody = BlockStatement(
            ExpressionStatement(
                OperatorExpression(
                    FieldAccess(
                        IdentifierExpression("this"),
                        IdentifierExpression("delegate")
                    ),
                    Operator.ASSING,
                    IdentifierExpression("delegate")
                )
            )
        )

        val constructor = method()
            .withKind(ElementKind.CONSTRUCTOR)
            .withModifier(Modifier.PUBLIC)
            .withSimpleName(controllerName)
            .withParameter(
                parameter()
                    .withModifier(Modifier.FINAL)
                    .withType(DeclaredType(delegateName))
                    .withSimpleName("delegate")
                    .build()
            )
            .withBody(constructorBody)
            .build()

        val qualifiedApiName = "${packageElement.getQualifiedName()}.${createApiName(url)}"

        apiBuilder.withEnclosedElement(constructor)
        apiBuilder.withKind(ElementKind.CLASS)
        apiBuilder.withSimpleName(controllerName)
        apiBuilder.withInterface(DeclaredType(qualifiedApiName))
        apiBuilder.withEnclosingElement(packageElement)
    }

    override fun getElementKind(): ElementKind {
        return ElementKind.CLASS
    }

    override fun createClassName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() } + "Controller"
    }

    private fun createControllerName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() } + "Controller"
    }

    private fun createDelegateName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() } + "Delegate"
    }

    override fun buildMethod(httpMethod: HttpMethod, url: String, operation: Operation, methodBuilder: MethodBuilder) {
        methodBuilder.withModifier(Modifier.PUBLIC)
        methodBuilder.withAnnotation(createOverrideAnnotation())

        val body = when(httpMethod) {
            HttpMethod.POST -> createPostMethodBody(httpMethod, operation, hasBody(operation))
            HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE -> createPutOrDeleteMethodBody(httpMethod, operation, hasBody(operation))
            HttpMethod.GET -> createGetMethodBody(httpMethod, url, operation, hasBody(operation))
        }

        methodBuilder.withBody(body)

        val delegateReturnType: Type = when (httpMethod) {
            HttpMethod.POST -> {

                val idProperty = idSchemas[url]

                if (idProperty != null) {
                    val idSchema = idProperty.schema
                    typeConverter.convert(idSchema.type, idSchema)
                } else {
                    DeclaredType("java.lang.Object")
                }
            }
            HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE -> PrimitiveType.VOID
            else -> { //GET
                val rt = (createReturnType(operation) as DeclaredType).typeArgs.first()
                val urlElements = url.split("/")
                if (isPathVariable(urlElements.last())) {
                    DeclaredType("java.util.Optional", listOf(rt))
                } else {
                    rt
                }
            }
        }

        delegateGenerator.addMethod(
            delegateReturnType,
            createMethodName(httpMethod, operation),
            createMethodParameters(httpMethod, url, operation)
        )
    }

    private fun isPathVariable(value: String): Boolean {
        return value.startsWith("{") && value.endsWith("}")
    }

    private fun hasBody(operation: Operation): Boolean {
        return getRequestBodySchema(operation) != null
    }

    private fun createPostMethodBody(httpMethod: HttpMethod,
                                     operation: Operation,
                                     hasBody: Boolean): BlockStatement {
        val statements = ArrayList<Statement>()
        val methodName = createMethodName(httpMethod, operation)

        val delegateInvoke = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression("delegate"),
                    IdentifierExpression(methodName)
                )
            )

        delegateInvoke.withParameters(createPathVariableParameterExpressions(operation))

        if (hasBody) {
            delegateInvoke.withParameter(IdentifierExpression("body"))
        }

        delegateInvoke.withParameter(IdentifierExpression("httpServletRequest"))

        val createLocationCall =  methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression(
                        "${apiPackage}.ApiUtils",
                        DeclaredType("${apiPackage}.ApiUtils")
                    ),
                    IdentifierExpression("createLocation"),
                )
            )
            .withParameter(IdentifierExpression("httpServletRequest"))
            .withParameter(delegateInvoke.build())
            .build()

        val mi = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression(
                        "org.springframework.http.ResponseEntity",
                        DeclaredType("org.springframework.http.ResponseEntity")
                    ),
                    IdentifierExpression("created")
                )
            )
            .withParameter(createLocationCall)
            .build()

        val invokeBuild = methodInvocation()
            .withSelect(FieldAccess(mi, IdentifierExpression("build")))

        statements += ReturnStatement(invokeBuild.build())
        return BlockStatement(statements)
    }

    private fun createPutOrDeleteMethodBody(httpMethod: HttpMethod,
                                            operation: Operation,
                                            hasBody: Boolean): Statement {
        val parameters = mutableListOf<Expression>()
        parameters += createPathVariableParameterExpressions(operation)

        if (hasBody) {
            val bodyIdentifier = createBodyParameterIdentifier(operation)
            if (bodyIdentifier != null) {
                parameters += bodyIdentifier
            }
        }

        parameters += createHttpServletRequestParameterIdentifier()

        val methodName = createMethodName(httpMethod, operation)

        val invokeDelegate = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression("delegate"),
                    IdentifierExpression(methodName)
                )
            )
            .withParameters(parameters)
            .build()

        val response = operation.responses.entries.first { is2XX(it.key) }

        val responseCode = response.key
        val responseEntityMethodName = if (responseCode == "204") "noContent" else "ok"

        val returnStatement = ReturnStatement(methodInvocation()
            .withSelect(
                FieldAccess(
                    methodInvocation()
                        .withSelect(
                            FieldAccess(
                                IdentifierExpression(
                                    "org.springframework.http.ResponseEntity",
                                    DeclaredType("org.springframework.http.ResponseEntity")),
                                IdentifierExpression(responseEntityMethodName)
                            )
                        )
                        .build(),
                    IdentifierExpression("build")
                )
            ).build()
        )

        return BlockStatement(
            ExpressionStatement(invokeDelegate),
            returnStatement
        )
    }

    private fun createGetMethodBody(httpMethod: HttpMethod,
                                    url: String,
                                    operation: Operation,
                                    hasBody: Boolean): Statement {
        val parameters = mutableListOf<Expression>()
        parameters += createPathVariableParameterExpressions(operation)

        if (hasBody) {
            val bodyIdentifier = createBodyParameterIdentifier(operation)
            if (bodyIdentifier != null) {
                parameters += bodyIdentifier
            }
        }

        parameters += createHttpServletRequestParameterIdentifier()

        val methodName = createMethodName(httpMethod, operation)

        val invokeDelegate = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression("delegate"),
                    IdentifierExpression(methodName)
                )
            )
            .withParameters(parameters)
            .build()

        if (isPathVariable(url.split("/").last())) {
            val invokeOk = methodInvocation()
                .withSelect(
                    FieldAccess(
                        IdentifierExpression("org.springframework.http.ResponseEntity",
                            DeclaredType("org.springframework.http.ResponseEntity")),
                        IdentifierExpression("ok")
                    )
                ).withParameter(IdentifierExpression("it"))
                .build()

            val invokeMap = methodInvocation()
                .withSelect(
                    FieldAccess(
                        invokeDelegate,
                        IdentifierExpression("map")
                    )
                ).withParameter(
                    LambdaExpression(
                        parameters = listOf(IdentifierExpression("it")),
                        body = invokeOk
                    )
                ).build()

            val invokeNotFound = methodInvocation()
                .withSelect(
                    FieldAccess(
                        IdentifierExpression("org.springframework.http.ResponseEntity", DeclaredType("org.springframework.http.ResponseEntity")),
                        IdentifierExpression("notFound")
                    )
                ).build()

            val invokeBuild = methodInvocation()
                .withSelect(
                    FieldAccess(
                        invokeNotFound,
                        IdentifierExpression("build")
                    )
                ).build()

            val invokeOrElse = methodInvocation()
                .withSelect(
                    FieldAccess(
                        invokeMap,
                        IdentifierExpression("orElse")
                    )
                ).withParameter(
                    invokeBuild
                ).build()

            return ReturnStatement(invokeOrElse)
        } else {
            val returnStatement = ReturnStatement(
                methodInvocation()
                    .withSelect(
                        FieldAccess(
                            IdentifierExpression(
                                "org.springframework.http.ResponseEntity",
                                DeclaredType("org.springframework.http.ResponseEntity")),
                            IdentifierExpression("ok")
                        )
                    )
                    .withParameter(invokeDelegate)
                    .build()
            )

            return BlockStatement(returnStatement)
        }
    }

    private fun createOverrideAnnotation(): Annotation {
        return annotation()
            .withType(DeclaredType("java.lang.Override"))
            .build()
    }

    private fun createPathVariableParameterExpressions(operation: Operation): List<Expression> {
        return operation.parameters
            .map { uriParameter ->
                IdentifierExpression(uriParameter.name)
            }
    }

    private fun createBodyParameterIdentifier(operation: Operation): IdentifierExpression? {
        val requestBody = operation.requestBody
        val bodyRequired = requestBody.isRequired

        val paramAnnotation = annotation()
            .withType(DeclaredType("org.springframework.web.bind.annotation.RequestBody"))

        if (bodyRequired) {
            paramAnnotation.withAttribute(Attribute.of("required", bodyRequired))
        }

        val schema = getRequestBodySchema(operation)
        return if (schema != null) IdentifierExpression("body") else null
    }

    private fun createHttpServletRequestParameterIdentifier(): IdentifierExpression {
        return IdentifierExpression("httpServletRequest")
    }

    private fun getRequestBodySchema(operation: Operation): Schema? {
        val contentMediaType = operation.requestBody.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)
        return contentMediaType?.schema
    }

    private fun createApiName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() } + "Api"
    }

    override fun finish() {
        filer.createSource(apiBuilder.build())
        delegateGenerator.finish(filer)
    }
}