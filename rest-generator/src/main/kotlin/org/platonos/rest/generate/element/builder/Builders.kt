package org.platonos.rest.generate.element.builder

import org.platonos.rest.generate.element.ElementKind
import org.platonos.rest.generate.expression.MethodInvocationBuilder
import org.platonos.rest.generate.statement.VariableDeclarationBuilder

object Builders {

    fun field(): VariableElementBuilder {
        return VariableElementBuilder()
            .withKind(ElementKind.FIELD)
    }

    fun parameter(): VariableElementBuilder {
        return VariableElementBuilder()
            .withKind(ElementKind.PARAMETER)
    }

    fun method(): MethodBuilder {
        return MethodBuilder().withKind(ElementKind.METHOD)
    }

    fun typeElement(): TypeElementBuilder {
        return TypeElementBuilder().withKind(ElementKind.CLASS)
    }

    fun annotation(): AnnotationBuilder {
        return AnnotationBuilder()
    }

    fun methodInvocation(): MethodInvocationBuilder {
        return MethodInvocationBuilder()
    }

    fun variableDeclaration(): VariableDeclarationBuilder {
        return VariableDeclarationBuilder()
    }

}