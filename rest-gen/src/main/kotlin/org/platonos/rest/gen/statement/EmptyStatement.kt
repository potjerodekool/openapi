package org.platonos.rest.gen.statement

import org.platonos.rest.gen.TreeVisitor

class EmptyStatement : Statement() {

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitEmptyStatement(this, param)
    }

    override fun toString(): String {
        return ";"
    }
}