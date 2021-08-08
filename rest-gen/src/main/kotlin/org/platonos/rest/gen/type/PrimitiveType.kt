package org.platonos.rest.gen.type

import org.platonos.rest.gen.TreeVisitor

class PrimitiveType(private val name: String, private val kind: TypeKind) : Type {

    companion object {
        val BOOLEAN = PrimitiveType("boolean", TypeKind.BOOLEAN)
        val INT = PrimitiveType("int", TypeKind.INT)
        val LONG = PrimitiveType("long", TypeKind.INT)
        val FLOAT = PrimitiveType("float", TypeKind.INT)
        val DOUBLE = PrimitiveType("double", TypeKind.INT)
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

}