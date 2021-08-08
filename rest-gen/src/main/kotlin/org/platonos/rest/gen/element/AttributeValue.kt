package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor

interface AttributeValue {

    fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R
}