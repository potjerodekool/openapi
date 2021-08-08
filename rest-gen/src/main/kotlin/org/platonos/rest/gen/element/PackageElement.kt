package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor

class PackageElement(val packageName: String) : AbstractElement<PackageElement>(), QualifiedNameable {

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitPackage(this, param)
    }

    override fun getQualifiedName(): String {
        return packageName
    }

    override fun toString(): String {
        return packageName
    }
}