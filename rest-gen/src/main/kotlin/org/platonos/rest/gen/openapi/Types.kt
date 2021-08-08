package org.platonos.rest.gen.openapi

import org.platonos.rest.gen.type.Type

interface Types {

    fun getNumber(nullable: Boolean): Type

    fun getInteger(nullable: Boolean): Type

    fun getLong(nullable: Boolean): Type

    fun getFloat(nullable: Boolean): Type

    fun getDouble(nullable: Boolean): Type

    fun getString(nullable: Boolean): Type

    fun getBoolean(nullable: Boolean): Type
}