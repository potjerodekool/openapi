package org.platonos.rest.generate.openapi

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.type.Type

interface TypeConverter {

    fun convert(type: String, schema: Schema): Type

    fun isPlatformType(type: Type): Boolean

    fun isOpenApiType(schemaName: String): Boolean

    fun isOpenApiType(schema: Schema): Boolean
}