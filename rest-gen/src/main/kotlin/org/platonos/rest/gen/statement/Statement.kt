package org.platonos.rest.gen.statement

import org.platonos.rest.gen.TreeVisitor

abstract class Statement {

    abstract fun <P,R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R
}