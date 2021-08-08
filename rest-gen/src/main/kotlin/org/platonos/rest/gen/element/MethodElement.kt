package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.doc.JavaDoc
import org.platonos.rest.gen.element.builder.MethodBuilder

class MethodElement(methodBuilder: MethodBuilder): AbstractElement<MethodElement>(methodBuilder) {

    val returnType = methodBuilder.returnType
    val body = methodBuilder.body
    val parameters = methodBuilder.parameters
    val javaDoc: JavaDoc? = methodBuilder.javaDoc

    val hasAbstractModifier: Boolean
    get() {
        if (modifiers.contains(Modifier.ABSTRACT)) {
            return true
        } else {
            val ee = enclosingElement
            return if (ee != null) ee.kind == ElementKind.INTERFACE else false
        }
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