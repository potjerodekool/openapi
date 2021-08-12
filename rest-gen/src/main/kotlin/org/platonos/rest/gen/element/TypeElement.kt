package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor
import org.platonos.rest.gen.element.ElementFilter.fields
import org.platonos.rest.gen.element.ElementFilter.methods
import org.platonos.rest.gen.element.builder.TypeElementBuilder
import org.platonos.rest.gen.type.DeclaredType

class TypeElement(builder: TypeElementBuilder):
    AbstractElement<TypeElement>(builder), QualifiedNameable, Cloneable {

    val classKind: Boolean
    get() { return kind == ElementKind.CLASS }

    val interfaceKind: Boolean
    get() { return  kind == ElementKind.INTERFACE }

    val fields: List<VariableElement>
    get() {
        return fields(enclosedElements)
    }

    val methods: List<MethodElement>
        get() {
            return methods(enclosedElements)
        }

    val interfaces: List<DeclaredType> = builder.interfaces

    val hasInterfaces: Boolean = interfaces.isNotEmpty()

    override fun toString(): String {
        return simpleName
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitTypeElement(this, param)
    }

    override fun getQualifiedName(): String {
        val ownerName = enclosingElement.toString()
        return "${ownerName}.$simpleName"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is TypeElement) getQualifiedName() == other.getQualifiedName() else false
    }

    override fun hashCode(): Int {
        return getQualifiedName().hashCode()
    }

    fun builder(): TypeElementBuilder {
        return TypeElementBuilder(this)
    }

}