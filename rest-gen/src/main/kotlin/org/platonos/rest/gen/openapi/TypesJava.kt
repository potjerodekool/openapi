package org.platonos.rest.gen.openapi

import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.PrimitiveType
import org.platonos.rest.gen.type.Type

class TypesJava : Types {

    companion object {
        private val NUMBER = DeclaredType(java.lang.Number::class.java.name)
        private val BOOLEAN = DeclaredType(java.lang.Boolean::class.java.name)
        private val INTEGER = DeclaredType(java.lang.Integer::class.java.name)
        private val LONG = DeclaredType(java.lang.Long::class.java.name)
        private val STRING = DeclaredType(java.lang.String::class.java.name)
        private val FLOAT = DeclaredType(java.lang.Float::class.java.name)
        private val DOUBLE = DeclaredType(java.lang.Double::class.java.name)
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

}