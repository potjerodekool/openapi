package org.platonos.rest.gen

interface AstTree {

    fun <P,R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R
}