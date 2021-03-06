package org.platonos.rest.generate.statement

import org.platonos.rest.generate.element.Modifier
import org.platonos.rest.generate.expression.Expression
import org.platonos.rest.generate.type.Type

class VariableDeclarationBuilder {

    val modifiers = mutableSetOf<Modifier>()
    var type: Type? = null
    var name: String = ""
    var init: Expression? = null

    fun withModifier(modifier: Modifier): VariableDeclarationBuilder {
        this.modifiers += modifier
        return this
    }

    fun withModifiers(modifiers: Set<Modifier>): VariableDeclarationBuilder {
        this.modifiers += modifiers
        return this
    }

    fun withType(type: Type?): VariableDeclarationBuilder {
        this.type = type
        return this
    }

    fun withName(name: String): VariableDeclarationBuilder {
        this.name = name
        return this
    }

    fun withInit(init: Expression?): VariableDeclarationBuilder {
        this.init = init
        return this
    }

    fun build(): VariableDeclaration {
        return VariableDeclaration(this)
    }
}