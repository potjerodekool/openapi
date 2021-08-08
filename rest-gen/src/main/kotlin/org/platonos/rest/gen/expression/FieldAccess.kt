package org.platonos.rest.gen.expression

class FieldAccess(private val target: Expression? = null,
                  private val fieldExpression: Expression): Expression() {

    override fun toString(): String {
        if (target != null) {
            return "${target}.${fieldExpression}"
        } else {
            return fieldExpression.toString()
        }
    }
}