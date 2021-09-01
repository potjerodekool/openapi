package org.platonos.rest.generate.openapi

import org.platonos.rest.generate.type.Type

interface Types {

    fun getNumber(nullable: Boolean): Type

    fun getInteger(nullable: Boolean): Type

    fun getLong(nullable: Boolean): Type

    fun getFloat(nullable: Boolean): Type

    fun getDouble(nullable: Boolean): Type

    fun getString(nullable: Boolean): Type

    fun getBoolean(nullable: Boolean): Type
}