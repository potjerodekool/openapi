package org.platonos.rest.gen.element

import org.platonos.rest.gen.TreeVisitor

class CompilationUnit(val packageElement: PackageElement,
                      val typeElement: TypeElement,
                      val imports: List<Import> = emptyList()): Visitable {

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitCompilationUnit(this, param)
    }

}