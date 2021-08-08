package org.platonos.rest.gen.element.builder

import org.platonos.rest.gen.element.TypeElement

class TypeElementBuilder : AbstractElementBuilder<TypeElementBuilder> {

    constructor(): super()

    constructor(typeElement: TypeElement): super(typeElement)

    override fun build(): TypeElement {
        return TypeElement(this)
    }

}
