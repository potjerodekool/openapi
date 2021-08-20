package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.type.DeclaredType

class Annotation(val type: DeclaredType, val attributes: List<Attribute> = emptyList()) {

    fun withAttribute(attribute: Attribute): Annotation {
        val attributes = attributes.toMutableList()
        attributes += attribute
        return Annotation(type, attributes)
    }

    fun withValue(value: String): Annotation {
        return withAttribute(Attribute.of("value", value))
    }

    fun <P,R> accept(treeVisitor: TreeVisitor<P,R>, param: P): R {
        return treeVisitor.visitAnnotation(this, param)
    }

    override fun toString(): String {
        val attrString = attributes.joinToString(separator = ",")
        return "@$type($attrString)"
    }
}