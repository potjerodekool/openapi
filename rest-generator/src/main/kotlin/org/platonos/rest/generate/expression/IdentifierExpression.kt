package org.platonos.rest.generate.expression

import org.platonos.rest.generate.TreeVisitor
import org.platonos.rest.generate.type.Type

class IdentifierExpression(val name: String, val type: Type? = null) : Expression() {

    override fun toString(): String {
        return name
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitIdentifierExpression(this, param)
    }
}