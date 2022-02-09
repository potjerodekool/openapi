package org.platonos.rest.generate.type

import org.platonos.rest.generate.TreeVisitor

class PrimitiveType(private val name: String, private val kind: TypeKind) : Type {

    companion object {
        val BOOLEAN = PrimitiveType("boolean", TypeKind.BOOLEAN)
        val SHORT = PrimitiveType("short", TypeKind.SHORT)
        val CHAR = PrimitiveType("char", TypeKind.CHAR)
        val INT = PrimitiveType("int", TypeKind.INT)
        val LONG = PrimitiveType("long", TypeKind.LONG)
        val FLOAT = PrimitiveType("float", TypeKind.FLOAT)
        val DOUBLE = PrimitiveType("double", TypeKind.DOUBLE)
        val VOID = PrimitiveType("void", TypeKind.VOID)
    }

    override fun getSimpleName(): String {
        return name
    }

    override fun toString(): String {
        return name
    }

    override fun getKind(): TypeKind {
        return kind
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitPrimitiveType(this, param)
    }

    override fun isPrimitive(): Boolean {
        return true
    }

}