package org.platonos.rest.gen.expression

class NameExpression(val name: String) : Expression() {

    override fun toString(): String {
        return name
    }
}