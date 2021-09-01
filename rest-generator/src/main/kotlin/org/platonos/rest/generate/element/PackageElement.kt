package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor

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