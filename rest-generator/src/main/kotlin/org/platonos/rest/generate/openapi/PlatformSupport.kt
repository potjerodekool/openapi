package org.platonos.rest.generate.openapi

import org.platonos.rest.generate.type.Type

interface PlatformSupport {

    fun getTypeConverter(): TypeConverter

    fun getSourceFileExtension(): String

    fun isPlatformType(type: Type): Boolean
}