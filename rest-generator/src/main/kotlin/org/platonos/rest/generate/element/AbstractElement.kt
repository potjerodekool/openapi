package org.platonos.rest.generate.element

import org.platonos.rest.generate.element.builder.AbstractElementBuilder
import java.lang.UnsupportedOperationException

abstract class AbstractElement<E : AbstractElement<E>>(builder: AbstractElementBuilder<*>) : Visitable {

    constructor(): this(DummyBuilder)

    val simpleName = builder.simpleName
    val type = builder.type

    private var _enclosingElement: AbstractElement<*>? = builder.enclosingElement
    val enclosingElement: AbstractElement<*>?
    get() {
        return _enclosingElement
    }

    val enclosedElements = linkEnclosedElements(builder.enclosedElements)
    val annotations = builder.annotations
    val modifiers = builder.modifiers
    val kind = builder.kind

    val hasModifiers: Boolean
    get() = modifiers.isNotEmpty()

    val hasAnnotations: Boolean
    get() = annotations.isNotEmpty()

    private fun linkEnclosedElements(enclosedElements: MutableList<AbstractElement<*>>): List<AbstractElement<*>> {
        for (enclosedElement in enclosedElements) {
            enclosedElement._enclosingElement = this
        }

        return enclosedElements
    }

}

object DummyBuilder : AbstractElementBuilder<DummyBuilder>() {

    override fun build(): AbstractElement<*> {
        throw UnsupportedOperationException()
    }

}