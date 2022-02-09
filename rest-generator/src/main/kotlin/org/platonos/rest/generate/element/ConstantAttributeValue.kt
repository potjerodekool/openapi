package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor

class ConstantAttributeValue(private val constant: Any): AttributeValue {

    override fun toString(): String {
        return constant.toString()
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitConstantAttributeValue(this, param)
    }
}