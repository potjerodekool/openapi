package org.platonos.rest.gen.type

import org.platonos.rest.gen.TreeVisitor

object NoType : Type {

    override fun getSimpleName(): String {
        return "none"
    }

    override fun getKind(): TypeKind = TypeKind.NONE

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitNoType(this, param)
    }
}