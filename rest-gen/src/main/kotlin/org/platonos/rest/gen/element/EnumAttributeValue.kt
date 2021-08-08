package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.type.DeclaredType

class EnumAttributeValue(val type: DeclaredType, val enumConstant: String) : AttributeValue {

    override fun toString(): String {
        return "$type.$enumConstant"
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitEnumAttributeValue(this, param)
    }

}