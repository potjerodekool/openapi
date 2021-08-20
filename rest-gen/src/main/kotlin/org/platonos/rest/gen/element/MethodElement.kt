package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.doc.JavaDoc
import org.platonos.rest.gen.element.builder.MethodBuilder

class MethodElement(methodBuilder: MethodBuilder): AbstractElement<MethodElement>(methodBuilder) {

    val returnType = methodBuilder.returnType
    val body = methodBuilder.body
    val parameters = methodBuilder.parameters
    val javaDoc: JavaDoc? = methodBuilder.javaDoc

    val abstract: Boolean
    get() {
        return modifiers.contains(Modifier.ABSTRACT) || body == null
    }

    val constructor: Boolean
    get() {
        return kind == ElementKind.CONSTRUCTOR
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitMethod(this, param)
    }

    fun builder(): MethodBuilder {
        return MethodBuilder(this)
    }

    override fun toString(): String {
        if (constructor) {
            return "$simpleName(${parameters.joinToString()})"
        } else {
            return "$returnType $simpleName(${parameters.joinToString()})"
        }
    }
}