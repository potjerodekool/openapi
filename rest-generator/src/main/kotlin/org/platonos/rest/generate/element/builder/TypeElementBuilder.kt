package org.platonos.rest.generate.element.builder

import org.platonos.rest.generate.element.TypeElement
import org.platonos.rest.generate.type.DeclaredType

class TypeElementBuilder : AbstractElementBuilder<TypeElementBuilder> {

    val interfaces = mutableListOf<DeclaredType>()

    constructor(): super()

    constructor(typeElement: TypeElement): super(typeElement) {
        interfaces.addAll(typeElement.interfaces)
    }

    fun withInterface(interfaceType: DeclaredType): TypeElementBuilder {
        interfaces += interfaceType
        return this
    }

    override fun build(): TypeElement {
        return TypeElement(this)
    }

}
