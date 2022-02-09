package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor
import org.platonos.rest.generate.type.DeclaredType

class EnumAttributeValue(val type: DeclaredType, val enumConstant: String) : AttributeValue {

    constructor(typeName: String, enumConstant: String): this(DeclaredType(typeName), enumConstant)

    override fun toString(): String {
        return "$type.$enumConstant"
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitEnumAttributeValue(this, param)
    }

}