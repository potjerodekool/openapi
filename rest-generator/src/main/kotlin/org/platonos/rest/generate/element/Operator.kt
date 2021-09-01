package org.platonos.rest.generate.element

enum class Operator(val value: String) {

    ASSING("=");

    override fun toString(): String {
        return value
    }

}