package org.platonos.rest.generate.type

import org.platonos.rest.generate.TreeVisitor

class DeclaredType(private val qualifiedName: String,
                   val enclosingType: DeclaredType? = null,
                   val typeArgs: List<Type> = emptyList()) : Type {

    constructor(qualifiedName: String,
                typeArgs: List<Type> = emptyList()): this(qualifiedName, null, typeArgs)

    private val simpleName: String

    init {
        val sepIndex = qualifiedName.lastIndexOf('.')
        simpleName = if (sepIndex < 0) qualifiedName else qualifiedName.substring(sepIndex + 1)
    }

    override fun getQualifiedName(): String {
        return qualifiedName
    }

    override fun getSimpleName(): String {
        return simpleName
    }

    override fun toString(): String {
        val typeName: String

        if (enclosingType != null) {
            typeName = "$enclosingType.$qualifiedName"
        } else {
            typeName = qualifiedName
        }

        return if (typeArgs.isEmpty()) {
            typeName
        } else {
            typeName + typeArgs.joinToString(prefix = "<", separator = ", ", postfix = ">")
        }
    }

    override fun getKind(): TypeKind {
        return TypeKind.DECLARED
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitDeclaredType(this, param)
    }

    fun nested(declaredType: DeclaredType): DeclaredType {
        return DeclaredType(
            declaredType.qualifiedName,
            this,
            declaredType.typeArgs
        )
    }

}