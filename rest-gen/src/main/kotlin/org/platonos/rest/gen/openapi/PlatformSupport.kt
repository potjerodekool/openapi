package org.platonos.rest.gen.openapi

import org.platonos.rest.gen.type.Type

interface PlatformSupport {

    fun getTypeConverter(): TypeConverter

    fun getSourceFileExtension(): String

    fun isPlatformType(type: Type): Boolean
}