package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.doc.JavaDoc
import org.platonos.rest.gen.element.builder.MethodBuilder

class MethodElement(methodBuilder: MethodBuilder): AbstractElement<MethodElement>(methodBuilder) {

    val returnType = methodBuilder.returnType
    val body = methodBuilder.body
    val parameters = methodBuilder.parameters
    val javaDoc: JavaDoc? = methodBuilder.javaDoc

    val isAbstract: Boolean
    get() {
        return modifiers.contains(Modifier.ABSTRACT)
    }

    override fun <P, R> accept(visitor: TreeVisitor<P, R>, param: P): R {
        return visitor.visitMethod(this, param)
    }

    fun builder(): MethodBuilder {
        return MethodBuilder(this)
    }

    override fun toString(): String {
        return "$returnType $simpleName(${parameters.joinToString()})"
    }
}