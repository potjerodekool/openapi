package org.platonos.rest.gen.element.builder

import org.platonos.rest.gen.element.ElementKind

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
}