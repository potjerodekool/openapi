package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor

interface Visitable {

    fun <P,R> accept(visitor: TreeVisitor<P, R>, param: P): R
}