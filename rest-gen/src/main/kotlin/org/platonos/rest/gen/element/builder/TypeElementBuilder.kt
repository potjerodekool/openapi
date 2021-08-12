package org.platonos.rest.gen.element.builder

import org.platonos.rest.gen.element.TypeElement
import org.platonos.rest.gen.type.DeclaredType

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
