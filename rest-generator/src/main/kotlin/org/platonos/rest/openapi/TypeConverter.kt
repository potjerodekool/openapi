package org.platonos.rest.openapi

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.type.PrimitiveType
import org.platonos.rest.generate.type.Type

interface TypeConverter {

    fun convert(schema: Schema, isRequest: Boolean): Type {
        return convert(schema, isRequest, schema.isNullable)
    }

    fun convert(schema: Schema, isRequest: Boolean, isNullable: Boolean): Type

    fun isPlatformType(type: Type): Boolean

    fun isOpenApiType(schemaName: String): Boolean

    fun isOpenApiType(schema: Schema): Boolean
    fun unbox(primitiveType: PrimitiveType): Type
}