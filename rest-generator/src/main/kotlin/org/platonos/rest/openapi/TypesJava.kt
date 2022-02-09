package org.platonos.rest.openapi

import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.PrimitiveType
import org.platonos.rest.generate.type.Type

class TypesJava : Types {

    companion object {
        private val NUMBER = DeclaredType(java.lang.Number::class.java.name)
        private val BOOLEAN = DeclaredType(java.lang.Boolean::class.java.name)
        private val SHORT = DeclaredType(java.lang.Short::class.java.name)
        private val CHAR = DeclaredType(java.lang.Character::class.java.name)
        private val INTEGER = DeclaredType(java.lang.Integer::class.java.name)
        private val LONG = DeclaredType(java.lang.Long::class.java.name)
        private val STRING = DeclaredType(java.lang.String::class.java.name)
        private val FLOAT = DeclaredType(java.lang.Float::class.java.name)
        private val DOUBLE = DeclaredType(java.lang.Double::class.java.name)
        private val VOID = PrimitiveType.VOID
    }

    override fun getNumber(nullable: Boolean): Type {
        return NUMBER
    }

    override fun getInteger(nullable: Boolean): Type {
        return if (nullable) INTEGER else PrimitiveType.INT
    }

    override fun getLong(nullable: Boolean): Type {
        return if (nullable) LONG else PrimitiveType.LONG
    }

    override fun getString(nullable: Boolean): Type {
        return STRING
    }

    override fun getFloat(nullable: Boolean): Type {
        return if (nullable) FLOAT else PrimitiveType.FLOAT
    }

    override fun getDouble(nullable: Boolean): Type {
        return if (nullable) DOUBLE else PrimitiveType.DOUBLE
    }

    override fun getBoolean(nullable: Boolean): Type {
        return if (nullable) BOOLEAN else PrimitiveType.BOOLEAN
    }

    override fun getChar(nullable: Boolean): Type {
        return if (nullable) CHAR else PrimitiveType.CHAR
    }

    override fun getShort(nullable: Boolean): Type {
        return if (nullable) SHORT else PrimitiveType.SHORT
    }

    override fun getVoid(): Type {
        return VOID
    }

    override fun getDeclaredType(name: String, vararg typeArgs: Type): DeclaredType {
        return DeclaredType(name, *typeArgs)
    }

}