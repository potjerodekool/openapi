package org.platonos.rest.gen.expression

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.element.Operator

class OperatorExpression(val left: Expression,
                         val operator: Operator,
                         val right: Expression): Expression() {

    override fun toString(): String {
        return "$left $operator $right"
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitOperatorExpression(this, param)
    }
}