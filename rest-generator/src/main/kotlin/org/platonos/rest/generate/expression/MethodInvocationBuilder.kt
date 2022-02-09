package org.platonos.rest.generate.expression

import org.platonos.rest.generate.type.Type

class MethodInvocationBuilder {

    var methodSelect: Expression? = null
    val typeArgs = mutableListOf<Type>()
    val parameters = mutableListOf<Expression>()

    fun withSelect(methodSelect: Expression?): MethodInvocationBuilder {
        this.methodSelect = methodSelect
        return this
    }

    fun select(vararg select: String): MethodInvocationBuilder {
        select.forEach {
                methodSelect =
                if (methodSelect == null) IdentifierExpression(it)
                else FieldAccess(methodSelect, IdentifierExpression(it))
        }

        return this
    }

    fun withTypeArg(typeArg: Type): MethodInvocationBuilder {
        this.typeArgs += typeArg
        return this
    }

    fun withTypeArgs(vararg typeArgs: Type): MethodInvocationBuilder {
        this.typeArgs += typeArgs
        return this
    }

    fun withTypeArgs(typeArgs: List<Type>): MethodInvocationBuilder {
        this.typeArgs += typeArgs
        return this
    }

    fun withParameter(parameter: Expression): MethodInvocationBuilder {
        this.parameters += parameter
        return this
    }

    fun withParameters(vararg parameters: Expression): MethodInvocationBuilder {
        this.parameters += parameters
        return this
    }

    fun withParameters(parameters: List<Expression>): MethodInvocationBuilder {
        this.parameters += parameters
        return this
    }

    fun build(): MethodInvocation {
        return MethodInvocation(this)
    }
}