package org.platonos.rest.openapi

import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.Type

interface Types {

    fun getNumber(nullable: Boolean): Type

    fun getInteger(nullable: Boolean): Type

    fun getLong(nullable: Boolean): Type

    fun getFloat(nullable: Boolean): Type

    fun getDouble(nullable: Boolean): Type

    fun getString(nullable: Boolean): Type

    fun getBoolean(nullable: Boolean): Type

    fun getShort(nullable: Boolean): Type

    fun getChar(nullable: Boolean): Type

    fun getVoid(): Type

    fun getDeclaredType(name: String, vararg typeArgs: Type): DeclaredType


}