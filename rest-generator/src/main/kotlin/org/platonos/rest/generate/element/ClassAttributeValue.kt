package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor
import org.platonos.rest.generate.type.DeclaredType

class ClassAttributeValue(val declaredType: DeclaredType) : AttributeValue {

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitClassAttributeValue(this, param)
    }

    override fun toString(): String {
        return "${declaredType}.class"
    }
}