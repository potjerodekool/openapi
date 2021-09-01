package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor
import org.platonos.rest.generate.element.builder.VariableElementBuilder

class VariableElement(builder: VariableElementBuilder): AbstractElement<VariableElement>(builder) {

    val value = builder.value

    override fun toString(): String {
        if (value == null) {
            return "$type $simpleName"
        } else {
            return "$type $simpleName = $value"
        }
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitField(this, param)
    }

    fun builder(): VariableElementBuilder {
        return VariableElementBuilder(this)
    }

}