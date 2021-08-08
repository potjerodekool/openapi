package org.platonos.rest.gen.element.builder

import org.platonos.rest.gen.expression.Expression
import org.platonos.rest.gen.element.VariableElement

class VariableElementBuilder : AbstractElementBuilder<VariableElementBuilder> {

    private val enclosingBuilder: AbstractElementBuilder<*>?
    var value: Expression? = null

    constructor(): super() {
        this.enclosingBuilder = null
    }

    constructor(enclosingBuilder: AbstractElementBuilder<*>): super() {
        this.enclosingBuilder = enclosingBuilder
    }

    constructor(variableElement: VariableElement): super(variableElement) {
        this.enclosingBuilder = null
        this.value = variableElement.value
    }

    fun withValue(value: Expression): VariableElementBuilder {
        this.value = value
        return this
    }

    override fun build(): VariableElement {
        val variableElement = VariableElement(this)

        if (enclosingBuilder is MethodBuilder) {
            enclosingBuilder.withParameter(variableElement)
        }

        return variableElement
    }
}