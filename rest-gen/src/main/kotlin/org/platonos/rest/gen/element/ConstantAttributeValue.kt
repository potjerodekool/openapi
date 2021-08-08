package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor

class ConstantAttributeValue(val constant: Any): AttributeValue {

    override fun toString(): String {
        return constant.toString()
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitConstantAttributeValue(this, param)
    }
}