package org.platonos.rest.gen.statement

import org.platonos.rest.gen.expression.Expression

class ExpressionStatement(val expression: Expression) : Statement() {

    override fun toString(): String {
        return "$expression;"
    }
}