package org.platonos.rest.openapi

import org.platonos.rest.generate.type.Type

interface PlatformSupport {

    fun getModelNamingStrategy(): ModelNamingStrategy

    fun getTypeConverter(): TypeConverter

    fun getSourceFileExtension(): String

    fun isPlatformType(type: Type): Boolean
}