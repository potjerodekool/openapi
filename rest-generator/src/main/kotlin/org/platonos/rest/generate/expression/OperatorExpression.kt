package org.platonos.rest.generate.expression

import org.platonos.rest.generate.TreeVisitor
import org.platonos.rest.generate.element.Operator

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