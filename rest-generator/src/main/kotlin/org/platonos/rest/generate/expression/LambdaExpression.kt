package org.platonos.rest.generate.expression

import org.platonos.rest.generate.AstTree
import org.platonos.rest.generate.TreeVisitor

class LambdaExpression(val parameters: List<Expression>, val body: AstTree) : Expression() {

    override fun toString(): String {
        val paramsString = parameters.joinToString(prefix = "(", separator = ",", postfix = ")")
        return "$paramsString -> $body"
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitLambaExpression(this, param)
    }
}