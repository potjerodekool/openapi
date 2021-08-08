package org.platonos.rest.gen.expression

import org.platonos.rest.gen.type.Type
import java.lang.StringBuilder

class MethodInvocation(val target: Expression?,
                       val name: String,
                       val typeArgs: List<Type> = emptyList()) : Expression() {

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        if (target != null) {
            stringBuilder.append(target).append(".")
        }

        stringBuilder.append(name)

        if (typeArgs.isNotEmpty()) {
            stringBuilder.append(typeArgs.joinToString(prefix = "<", separator = ",", postfix = ">"))
        }

        stringBuilder.append("()")
        return stringBuilder.toString()
    }
}