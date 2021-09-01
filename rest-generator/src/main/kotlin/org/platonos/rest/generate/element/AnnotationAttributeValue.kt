package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor

class AnnotationAttributeValue(val annotation: Annotation) : AttributeValue {

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitAnnotationAttributeValue(this, param)
    }

    override fun toString(): String {
        return annotation.toString()
    }
}