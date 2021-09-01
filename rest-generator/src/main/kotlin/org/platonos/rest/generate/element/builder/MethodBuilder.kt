package org.platonos.rest.generate.element.builder

import org.platonos.rest.generate.doc.JavaDoc
import org.platonos.rest.generate.element.MethodElement
import org.platonos.rest.generate.element.VariableElement
import org.platonos.rest.generate.statement.BlockStatement
import org.platonos.rest.generate.statement.Statement
import org.platonos.rest.generate.type.PrimitiveType
import org.platonos.rest.generate.type.Type

class MethodBuilder : AbstractElementBuilder<MethodBuilder> {

    var returnType: Type = PrimitiveType.VOID
    var body: Statement? = null
    val parameters = mutableListOf<VariableElement>()
    var javaDoc: JavaDoc? = null

    constructor(): super()

    constructor(method: MethodElement): super(method) {
        returnType = method.returnType
        body = method.body
        parameters += method.parameters
        javaDoc = method.javaDoc
    }

    fun withReturnType(returnType: Type): MethodBuilder {
        this.returnType = returnType
        return this
    }

    fun withBody(statement: Statement?): MethodBuilder {
        val newBody = if (statement == null || statement is BlockStatement) statement else BlockStatement(statement)
        this.body = newBody
        return this
    }

    fun withParameter(parameter: VariableElement): MethodBuilder {
        this.parameters += parameter
        return this
    }

    fun withParameters(parameters: List<VariableElement>): MethodBuilder {
        this.parameters += parameters
        return this
    }

    fun withParameter(): VariableElementBuilder {
        return VariableElementBuilder(this)
    }

    fun withoutParameters(): MethodBuilder {
        this.parameters.clear()
        return this
    }

    fun withJavaDoc(javaDoc: JavaDoc): MethodBuilder {
        this.javaDoc = javaDoc
        return this
    }

    override fun build(): MethodElement {
        return MethodElement(this)
    }

}