package org.platonos.rest.gen.expression

import org.platonos.rest.gen.AstTree
import org.platonos.rest.gen.TreeVisitor

class LambdaExpression(val parameters: List<Expression>, val body: AstTree) : Expression() {

    override fun toString(): String {
        val paramsString = parameters.joinToString(prefix = "(", separator = ",", postfix = ")")
        return "$paramsString -> $body"
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitLambaExpression(this, param)
    }
}