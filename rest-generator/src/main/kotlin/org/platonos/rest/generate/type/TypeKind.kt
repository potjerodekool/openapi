package org.platonos.rest.generate.type

enum class TypeKind {

    BOOLEAN,
    SHORT,
    CHAR,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    DECLARED,
    VOID,
    NONE;

    fun isPrimitive(): Boolean {
        return when(this) {
            BOOLEAN,
                SHORT,
                CHAR,
                INT,
                LONG,
                FLOAT,
                DOUBLE -> true
            else -> false
        }
    }
}