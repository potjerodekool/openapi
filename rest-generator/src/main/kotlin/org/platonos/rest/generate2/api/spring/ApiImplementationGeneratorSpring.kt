package org.platonos.rest.generate2.api.spring

import org.platonos.rest.backend.Filer
import org.platonos.rest.generate.element.*
import org.platonos.rest.generate.element.builder.Builders.annotation
import org.platonos.rest.generate.element.builder.Builders.field
import org.platonos.rest.generate.element.builder.Builders.method
import org.platonos.rest.generate.element.builder.Builders.methodInvocation
import org.platonos.rest.generate.element.builder.Builders.parameter
import org.platonos.rest.generate.element.builder.MethodBuilder
import org.platonos.rest.generate.element.builder.TypeElementBuilder
import org.platonos.rest.generate.expression.FieldAccess
import org.platonos.rest.generate.expression.IdentifierExpression
import org.platonos.rest.generate.expression.LambdaExpression
import org.platonos.rest.generate.expression.OperatorExpression
import org.platonos.rest.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.openapi.Types
import org.platonos.rest.openapi.api.*
import org.platonos.rest.generate.statement.BlockStatement
import org.platonos.rest.generate.statement.ExpressionStatement
import org.platonos.rest.generate.statement.ReturnStatement
import org.platonos.rest.generate.statement.Statement
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.Type
import org.platonos.rest.generate.type.TypeKind
import org.platonos.rest.generate2.api.ApiGenerator

class ApiImplementationGeneratorSpring(
    controllerName: String,
    private val delegateName: String,
    config: OpenApiGeneratorConfiguration,
    types: Types,
    filer: Filer) : AbstractApiGeneratorSpring(
    controllerName,
    config,
    types,
    filer), ApiGenerator {

    override fun initTypeElement(typeElementBuilder: TypeElementBuilder, controllerName: String) {
        val packageElement = typeElementBuilder.enclosingElement as PackageElement


        typeElementBuilder.simpleName = controllerName + "Impl"
        typeElementBuilder.withKind(ElementKind.CLASS)
        typeElementBuilder.withAnnotation(annotation().withType("org.springframework.web.bind.annotation.RestController").build())
        typeElementBuilder.withAnnotation(annotation().withType("org.springframework.web.bind.annotation.CrossOrigin").build())

        val delegateField = field()
            .withModifier(Modifier.PRIVATE)
            .withType(DeclaredType("${packageElement.getQualifiedName()}.${delegateName}"))
            .withSimpleName("delegate")
            .build()
        typeElementBuilder.withEnclosedElement(delegateField)

        val constructorBody = BlockStatement(
            ExpressionStatement(
                OperatorExpression(
                    FieldAccess(
                        IdentifierExpression("this"),
                        IdentifierExpression("delegate")
                    ),
                    Operator.ASSING,
                    IdentifierExpression("delegate")
                ))
        )

        val constructor = method()
            .withModifier(Modifier.PUBLIC)
            .withKind(ElementKind.CONSTRUCTOR)
            .withSimpleName(typeElementBuilder.simpleName)
            .withParameter(
                parameter()
                    .withType(DeclaredType("${packageElement.getQualifiedName()}.${delegateName}"))
                    .withSimpleName("delegate")
                    .build()
            )
            .withBody(constructorBody)
            .build()

        typeElementBuilder.withEnclosedElement(constructor)
    }

    override fun adjustMethod(methodBuilder: MethodBuilder) {
        super.adjustMethod(methodBuilder)
        methodBuilder.withModifier(Modifier.PUBLIC)
    }

    override fun adjustParameters(parameters: List<VariableElement>): List<VariableElement> {
        return parameters.map {
            it.builder().withModifier(Modifier.FINAL).build()
        }
    }

    override fun addMethodBody(
        httpMethod: HttpMethod,
        url: String,
        apiOperation: ApiOperation,
        methodBuilder: MethodBuilder) {

        val okCode = apiOperation.responses.keys.firstOrNull {
            is2xx(it)
        }

        val has404 = apiOperation.responses.containsKey("404")

        val parameters = methodBuilder.parameters
            .map { IdentifierExpression(it.simpleName) }

        if ("201" == okCode) {
            val invokeDelegate = methodInvocation()
                .withSelect(
                    FieldAccess(
                        IdentifierExpression("delegate"),
                        IdentifierExpression(methodBuilder.simpleName)
                    )
                ).withParameters(parameters)
                .build()

            val createLocation = methodInvocation()
                .withSelect(
                    FieldAccess(
                        IdentifierExpression("ApiUtils"),
                        IdentifierExpression("createLocation")
                    )
                )
                .withParameter(IdentifierExpression("request"))
                .withParameter(invokeDelegate)

            var invocation = methodInvocation()
                .withSelect(
                    FieldAccess(
                        IdentifierExpression(
                            "org.springframework.http.ResponseEntity",
                            DeclaredType("org.springframework.http.ResponseEntity")
                        ),
                        IdentifierExpression("created")
                    )
                ).withParameter(createLocation.build())
                .build()

            invocation = methodInvocation()
                .withSelect(
                    FieldAccess(
                        invocation,
                        IdentifierExpression("build")
                    )
                ).build()

            methodBuilder.withBody(ReturnStatement(invocation))
        } else {
            val invokeDelegate = methodInvocation()
                .withSelect(
                    FieldAccess(
                        IdentifierExpression("delegate"),
                        IdentifierExpression(methodBuilder.simpleName)
                    )
                ).withParameters(parameters)
                .build()

            if (isReturningVoid(methodBuilder.returnType)) {
                val statements = mutableListOf<Statement>()
                statements += ExpressionStatement(invokeDelegate)

                val invocation = methodInvocation()
                    .withSelect(
                        FieldAccess(
                            IdentifierExpression(
                                "org.springframework.http.ResponseEntity",
                                DeclaredType("org.springframework.http.ResponseEntity")
                            ),
                            IdentifierExpression("ok")
                        )
                    )
                    .build()

                statements += ReturnStatement(methodInvocation()
                    .withSelect(FieldAccess(
                        invocation,
                        IdentifierExpression("build")
                    )).build()
                )

                methodBuilder.withBody(BlockStatement(statements))
            } else {
                if (has404) {
                    val lambdaExpression = LambdaExpression(
                        listOf(IdentifierExpression("it")),
                        methodInvocation().withSelect(
                            FieldAccess(
                                IdentifierExpression("org.springframework.http.ResponseEntity"),
                                IdentifierExpression("ok"))
                        )
                            .withParameter(IdentifierExpression("it"))
                            .build()
                    )

                    var invocation = methodInvocation()
                        .withSelect(FieldAccess(invokeDelegate, IdentifierExpression("map")))
                        .withParameter(lambdaExpression)
                        .build()

                    invocation = methodInvocation().withSelect(FieldAccess(invocation, IdentifierExpression("orElseGet")))
                        .withParameter(LambdaExpression(
                            emptyList(),
                            methodInvocation()
                                .withSelect(
                                    FieldAccess(
                                        methodInvocation().withSelect(
                                            FieldAccess(
                                                IdentifierExpression("org.springframework.http.ResponseEntity"),
                                                IdentifierExpression("notFound")
                                            )
                                        ).build(),
                                        IdentifierExpression("build")
                                    )
                                ).build()
                        )).build()

                    methodBuilder.withBody(ReturnStatement(invocation))
                } else {
                    val invocation = methodInvocation()
                        .withSelect(
                            FieldAccess(
                                IdentifierExpression(
                                    "org.springframework.http.ResponseEntity",
                                    DeclaredType("org.springframework.http.ResponseEntity")
                                ),
                                IdentifierExpression("ok")
                            )
                        ).withParameter(invokeDelegate)
                        .build()
                    methodBuilder.withBody(ReturnStatement(invocation))
                }
            }
        }
    }

    private fun isReturningVoid(returnType: Type): Boolean {
        if (returnType.getKind() == TypeKind.VOID) {
            return true
        } else if (returnType.getKind() == TypeKind.DECLARED) {
            val declaredType = returnType as DeclaredType
            val typeArgs = declaredType.typeArgs

            return if (typeArgs.isEmpty()) {
                declaredType.getQualifiedName() == "java.lang.Void"
            } else {
                isReturningVoid(typeArgs.first())
            }
        }

        return false
    }

}