package org.platonos.rest.generate2.api.spring

import org.platonos.rest.backend.Filer
import org.platonos.rest.generate.element.*
import org.platonos.rest.generate.element.builder.Builders.methodInvocation
import org.platonos.rest.generate.element.builder.MethodBuilder
import org.platonos.rest.generate.element.builder.TypeElementBuilder
import org.platonos.rest.generate.expression.FieldAccess
import org.platonos.rest.generate.expression.IdentifierExpression
import org.platonos.rest.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.openapi.Types
import org.platonos.rest.openapi.api.*
import org.platonos.rest.generate.statement.ReturnStatement
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate2.api.ApiGenerator

class ApiDefinitionGeneratorSpring(
    controllerName: String,
    config: OpenApiGeneratorConfiguration,
    types: Types,
    filer: Filer) : AbstractApiGeneratorSpring(
    controllerName,
    config,
    types,
    filer), ApiGenerator {

    override fun initTypeElement(typeElementBuilder: TypeElementBuilder, controllerName: String) {
        typeElementBuilder.simpleName = controllerName
        typeElementBuilder.withKind(ElementKind.INTERFACE)
    }

    override fun adjustMethod(methodBuilder: MethodBuilder) {
        methodBuilder.withModifier(Modifier.DEFAULT)
    }

    override fun addMethodBody(
        httpMethod: HttpMethod,
        url: String,
        apiOperation: ApiOperation,
        methodBuilder: MethodBuilder) {
        methodBuilder.withModifier(Modifier.DEFAULT)

        val statusInvocation = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression(
                        "org.springframework.http.ResponseEntity",
                        DeclaredType("org.springframework.http.ResponseEntity")),
                    IdentifierExpression("status")
                )
            ).withParameter(
                FieldAccess(
                    IdentifierExpression(
                        "org.springframework.http.HttpStatus",
                        DeclaredType("org.springframework.http.HttpStatus")
                    ),
                    IdentifierExpression("NOT_IMPLEMENTED")
                )
            ).build()

        val buildInvocation = methodInvocation()
            .withSelect(
                FieldAccess(
                    statusInvocation,
                    IdentifierExpression("build")
                )
            ).build()

        val returnStatement = ReturnStatement(buildInvocation)
        methodBuilder.withBody(returnStatement)
    }

}