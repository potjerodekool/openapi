package org.platonos.rest.gen.element;

import org.platonos.rest.gen.TreeVisitor

class Attribute private constructor(val name: String, val value: AttributeValue) {

    companion object {

        fun of(key: String, value: String): Attribute {
            return Attribute(key, ConstantAttributeValue(quote(value)))
        }

        fun of(key: String, value: Boolean): Attribute {
            return Attribute(key, ConstantAttributeValue(value))
        }

        fun of(key: String, value: Int): Attribute {
            return Attribute(key, ConstantAttributeValue(value))
        }

        fun of(key: String, value: AttributeValue): Attribute {
            return Attribute(key, value)
        }

        private fun quote(s: String): String {
            return "\"$s\""
        }
    }

    override fun toString(): String {
        return "$name=$value"
    }

    fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitAttribute(this, param)
    }

}
