package org.platonos.rest.generate

interface AstTree {

    fun <P,R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R
}