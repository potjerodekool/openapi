package org.platonos.rest.gen.statement

class BlockStatement(val statements: List<Statement>) : Statement() {

    constructor(statement: Statement): this(listOf(statement))

    constructor(vararg statements: Statement): this(listOf(*statements))

    override fun toString(): String {
        return statements.joinToString(separator = "\n")
    }
}