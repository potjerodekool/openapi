package org.platonos.rest.generate.element

import org.platonos.rest.generate.TreeVisitor

class CompilationUnit(val packageElement: PackageElement,
                      val typeElement: TypeElement,
                      val imports: List<Import> = emptyList()): Visitable {

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitCompilationUnit(this, param)
    }

}