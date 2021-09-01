package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor

class ArrayAttributeValue(val values: List<AttributeValue> = emptyList()): AttributeValue {

    override fun toString(): String {
        return values.joinToString(prefix = "{", separator = "," , postfix = "}")
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitArrayAttributeValue(this, param)
    }
}