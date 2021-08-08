package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.type.Type

interface TypeConverter {

    fun convert(type: String, schema: Schema): Type

    fun isPlatformType(type: Type): Boolean

    fun isOpenApiType(schemaName: String): Boolean

    fun isOpenApiType(schema: Schema): Boolean
}