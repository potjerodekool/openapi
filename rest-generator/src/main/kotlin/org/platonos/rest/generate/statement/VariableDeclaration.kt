package org.platonos.rest.generate.statement

import org.platonos.rest.generate.TreeVisitor
import org.platonos.rest.generate.element.Modifier
import org.platonos.rest.generate.expression.Expression
import org.platonos.rest.generate.type.Type
import java.util.*

class VariableDeclaration(builder: VariableDeclarationBuilder) : Statement() {

    val modifiers: Set<Modifier> = builder.modifiers
    val type: Type? = builder.type
    val name: String = builder.name
    val init: Expression? = builder.init

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitVariableDeclation(this, param)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(modifiers.joinToString(separator = " ", postfix = " "))
        sb.append(type)
        sb.append(" ")
        sb.append(name)

        if (init != null) {
            sb.append(" = ").append(init)
        }

        sb.append(";")
        return sb.toString()
    }
}