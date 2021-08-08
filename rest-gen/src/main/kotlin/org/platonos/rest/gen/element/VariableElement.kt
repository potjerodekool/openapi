package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.element.builder.VariableElementBuilder

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