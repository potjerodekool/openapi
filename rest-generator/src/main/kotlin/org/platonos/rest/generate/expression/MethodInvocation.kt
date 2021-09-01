package org.platonos.rest.generate.expression

import org.platonos.rest.generate.TreeVisitor
import org.platonos.rest.generate.type.Type
import java.lang.StringBuilder

class MethodInvocation(methodInvocationBuilder: MethodInvocationBuilder) : Expression() {

    val methodSelect: Expression? = methodInvocationBuilder.methodSelect
    val typeArgs: List<Type> = methodInvocationBuilder.typeArgs
    val parameters: List<Expression> = methodInvocationBuilder.parameters

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        if (methodSelect != null) {
            stringBuilder.append(methodSelect)
        }

        if (typeArgs.isNotEmpty()) {
            stringBuilder.append(typeArgs.joinToString(prefix = "<", separator = ",", postfix = ">"))
        }

        stringBuilder.append("(")
        stringBuilder.append(parameters.joinToString(", "))
        stringBuilder.append(")")
        return stringBuilder.toString()
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitMethodInvocation(this, param)
    }
}