package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.type.DeclaredType

class ClassAttributeValue(val declaredType: DeclaredType) : AttributeValue {

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitClassAttributeValue(this, param)
    }

    override fun toString(): String {
        return "${declaredType}.class"
    }
}