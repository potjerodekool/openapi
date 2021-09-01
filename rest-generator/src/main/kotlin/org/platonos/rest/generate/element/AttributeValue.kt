package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor

interface AttributeValue {

    fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R
}