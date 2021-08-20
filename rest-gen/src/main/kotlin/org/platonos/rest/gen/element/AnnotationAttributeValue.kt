package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor

class AnnotationAttributeValue(val annotation: Annotation) : AttributeValue {

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitAnnotationAttributeValue(this, param)
    }

    override fun toString(): String {
        return annotation.toString()
    }
}