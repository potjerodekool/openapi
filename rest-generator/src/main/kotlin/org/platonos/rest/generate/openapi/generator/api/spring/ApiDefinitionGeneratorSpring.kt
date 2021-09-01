package org.platonos.rest.generate.openapi.generator.api.spring

import com.reprezen.kaizen.oasparser.model3.Operation
import org.platonos.rest.generate.element.ElementKind
import org.platonos.rest.generate.element.Modifier
import org.platonos.rest.generate.element.builder.Builders.methodInvocation
import org.platonos.rest.generate.element.builder.MethodBuilder
import org.platonos.rest.generate.expression.Expression
import org.platonos.rest.generate.expression.FieldAccess
import org.platonos.rest.generate.expression.IdentifierExpression
import org.platonos.rest.generate.openapi.api.HttpMethod
import org.platonos.rest.generate.openapi.generator.api.ApiDefinitionGenerator
import org.platonos.rest.generate.statement.ReturnStatement
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.util.Functions.replaceFirstChar

class ApiDefinitionGeneratorSpring : ApiDefinitionGenerator, AbstractSpringApiGenerator() {

    override fun createClassName(url: String): String {
        val start = url.lastIndexOf('/') + 1
        return url.substring(start).replaceFirstChar { it.uppercaseChar() } + "Api"
    }

    override fun getElementKind(): ElementKind {
        return ElementKind.INTERFACE
    }

    override fun buildMethod(httpMethod: HttpMethod,
                             url: String,
                             operation: Operation,
                             methodBuilder: MethodBuilder) {
        methodBuilder.withModifier(Modifier.DEFAULT)
        methodBuilder.withBody(ReturnStatement(responseEntityNotImplemented()))
    }

    private fun responseEntityNotImplemented(): Expression {
        val notFoundInvocation = methodInvocation()
            .withSelect(
                FieldAccess(
                    IdentifierExpression("org.springframework.http.ResponseEntity", DeclaredType("org.springframework.http.ResponseEntity")),
                    IdentifierExpression("status")
                )
            )
            .withParameter(
                FieldAccess(
                    IdentifierExpression(
                        "org.springframework.http.HttpStatus",
                        DeclaredType("org.springframework.http.HttpStatus")
                    ),
                    IdentifierExpression("NOT_IMPLEMENTED")
                )
            )
            .build()

        return methodInvocation()
            .withSelect(
                FieldAccess(
                    notFoundInvocation,
                    IdentifierExpression("build")
                )
            ).build()
    }

    override fun finish() {
        filer.createSource(apiBuilder.build())
    }

}