package org.platonos.rest.generate.type

import org.platonos.rest.generate.TreeVisitor

interface Type {

    fun isPrimitive(): Boolean = false

    fun getQualifiedName(): String {
        return getSimpleName()
    }

    fun getSimpleName(): String

    fun getKind(): TypeKind

    fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R
}