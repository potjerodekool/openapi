package org.platonos.rest.gen.openapi.generator.api.spring

import org.platonos.rest.gen.element.ElementKind
import org.platonos.rest.gen.element.Modifier
import org.platonos.rest.gen.element.PackageElement
import org.platonos.rest.gen.element.VariableElement
import org.platonos.rest.gen.element.builder.Builders.method
import org.platonos.rest.gen.element.builder.TypeElementBuilder
import org.platonos.rest.gen.openapi.generator.Filer
import org.platonos.rest.gen.type.Type

class DelegateGeneratorSpring(packageElement: PackageElement, simpleName: String) {

    private val typeElementBuilder = TypeElementBuilder()
        .withKind(ElementKind.INTERFACE)
        .withModifier(Modifier.PUBLIC)
        .withSimpleName(simpleName)
        .withEnclosingElement(packageElement)

    fun addMethod(returnType: Type,
                  methodName: String,
                  parameters: MutableList<VariableElement>) {

        typeElementBuilder.withEnclosedElement(
            method()
                .withReturnType(returnType)
                .withSimpleName(methodName)
                .withParameters(parameters)
                .build()
        )
    }

    fun finish(filer: Filer) {
        filer.createSource(typeElementBuilder.build())
    }
}