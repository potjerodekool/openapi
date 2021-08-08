package org.platonos.rest.gen.element

enum class Modifier(private val value: String) {

    PUBLIC("public"),
    PRIVATE("private"),
    FINAL("final"),
    ABSTRACT("abstract");

    override fun toString(): String {
        return value
    }
}