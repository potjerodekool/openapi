package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor

interface AttributeValue {

    fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R

    companion object {

        fun create(value: Any): AttributeValue {
            if (value is String) {
                return ConstantAttributeValue(Attribute.quote(value))
            } else {
                return TODO()
            }
        }
    }
}