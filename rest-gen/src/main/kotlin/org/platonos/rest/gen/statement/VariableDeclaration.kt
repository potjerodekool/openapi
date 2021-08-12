package org.platonos.rest.gen.statement

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.element.Modifier
import org.platonos.rest.gen.expression.Expression
import org.platonos.rest.gen.type.Type

class VariableDeclaration(builder: VariableDeclarationBuilder) : Statement() {

    val modifiers: Set<Modifier> = builder.modifiers
    val type: Type? = builder.type
    val name: String = builder.name
    val init: Expression? = builder.init

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitVariableDeclation(this, param)
    }
}