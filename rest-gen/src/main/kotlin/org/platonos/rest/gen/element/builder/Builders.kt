package org.platonos.rest.gen.element.builder

import org.platonos.rest.gen.element.ElementKind
import org.platonos.rest.gen.expression.MethodInvocationBuilder
import org.platonos.rest.gen.statement.VariableDeclarationBuilder

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