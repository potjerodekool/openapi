package org.platonos.rest.gen.statement

import org.platonos.rest.gen.expression.Expression

class ReturnStatement(val expression: Expression) : Statement() {

    override fun toString(): String {
        return "return $expression;"
    }
}