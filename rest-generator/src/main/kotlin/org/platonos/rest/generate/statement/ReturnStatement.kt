package org.platonos.rest.generate.statement

import org.platonos.rest.generate.TreeVisitor
import org.platonos.rest.generate.expression.Expression

class ReturnStatement(val expression: Expression) : Statement() {

    override fun toString(): String {
        return "return $expression;"
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitReturnStatement(this, param)
    }
}