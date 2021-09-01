package org.platonos.rest.generate.element

enum class Modifier(private val value: String) {

    PUBLIC("public"),
    PRIVATE("private"),
    FINAL("final"),
    ABSTRACT("abstract"),
    DEFAULT("default");

    override fun toString(): String {
        return value
    }
}