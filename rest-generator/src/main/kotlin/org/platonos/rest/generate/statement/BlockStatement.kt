package org.platonos.rest.generate.statement

import org.platonos.rest.generate.TreeVisitor

class BlockStatement(val statements: List<Statement>) : Statement() {

    constructor(statement: Statement): this(listOf(statement))

    constructor(vararg statements: Statement): this(listOf(*statements))

    override fun toString(): String {
        return statements.joinToString(separator = "\n")
    }

    override fun <P, R> accept(treeVisitor: TreeVisitor<P, R>, param: P): R {
        return treeVisitor.visitBlockStatement(this, param)
    }
}