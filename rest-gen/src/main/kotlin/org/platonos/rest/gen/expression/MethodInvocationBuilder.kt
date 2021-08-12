package org.platonos.rest.gen.expression

import org.platonos.rest.gen.type.Type

class MethodInvocationBuilder {

    var methodSelect: Expression? = null
    val typeArgs = mutableListOf<Type>()
    val parameters = mutableListOf<Expression>()

    fun withSelect(methodSelect: Expression?): MethodInvocationBuilder {
        this.methodSelect = methodSelect
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