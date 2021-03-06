package org.platonos.rest.generate.expression

import org.platonos.rest.generate.TreeVisitor

class FieldAccess(val target: Expression? = null,
                  val fieldExpression: Expression): Expression() {

    override fun toString(): String {
        if (target != null) {
            return "${target}.${fieldExpression}"
        } else {
            return fieldExpression.toString()
        }
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitFieldAccess(this, param)
    }
}