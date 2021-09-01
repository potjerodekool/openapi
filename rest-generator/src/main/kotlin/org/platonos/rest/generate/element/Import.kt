package org.platonos.rest.generate.element

class Import(val qualifiedName: String,
             val isStatic: Boolean = false) {

    override fun toString(): String {
        return qualifiedName
    }
}