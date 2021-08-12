package org.platonos.rest.gen.expression

import org.platonos.rest.gen.TreeVisitor

abstract class Expression {

    abstract fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R
}