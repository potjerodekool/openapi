package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor

class ArrayAttributeValue(val values: List<AttributeValue>): AttributeValue {

    override fun toString(): String {
        return values.joinToString(prefix = "{", separator = "," , postfix = "}")
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitArrayAttributeValue(this, param)
    }
}