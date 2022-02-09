package org.platonos.rest.openapi

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.type.DeclaredType
import org.platonos.rest.generate.type.PrimitiveType
import org.platonos.rest.generate.type.Type
import org.platonos.rest.generate.type.TypeKind
import org.platonos.rest.openapi.api.HttpMethod
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class JavaTypeConverter(
    private val types: TypesJava,
    private val modelNamingStrategy: ModelNamingStrategy,
    private val modelPackageName: String
) : TypeConverter {

    override fun convert(schema: Schema,
                         isRequest: Boolean,
                         isNullable: Boolean): Type {
        val type = schema.type
        val schemaFormat = schema.format

        return when(OpenApiType.fromType(type)) {
            OpenApiType.INTEGER -> {
                if (schemaFormat == "int32") {
                    return types.getInteger(isNullable)
                } else if (schemaFormat == "int64") {
                    return types.getLong(isNullable)
                } else {
                    throw OpenApiException("Schema with type integer should have format int32 or int64 but was $schemaFormat")
                }
            }
            OpenApiType.NUMBER -> {
                if (schemaFormat == "float") {
                    types.getFloat(isNullable)
                } else if (schemaFormat == "double") {
                    types.getDouble(isNullable)
                } else {
                    throw OpenApiException("Schema with type number should have format float or double but was $schemaFormat")
                }
            }
            OpenApiType.STRING -> {
                if (schemaFormat == "date") {
                    return DeclaredType(LocalDate::class.java.name)
                } else if (schemaFormat == "uuid") {
                    return DeclaredType(UUID::class.java.name)
                } else {
                    types.getString(isNullable)
                }
            }
            OpenApiType.DATE -> {
                return DeclaredType(LocalDate::class.java.name)
            }
            OpenApiType.DATE_TIME -> {
                return DeclaredType(LocalDateTime::class.java.name)
            }
            OpenApiType.BOOLEAN -> types.getBoolean(isNullable)
            OpenApiType.ARRAY -> {
                val itemsSchema = schema.itemsSchema
                DeclaredType(
                    "java.util.List",
                    listOf(convert(itemsSchema, isRequest))
                )
            }
            OpenApiType.OBJECT -> {
                return createObjectType(schema, isRequest)
            }
        }
    }

    private fun createObjectType(schema: Schema, isRequest: Boolean): Type {
        if (schema.additionalPropertiesSchema != null &&
            (schema.additionalPropertiesSchema.type != null)) {

            val keyType = types.getString(false)
            val valueType = convert(schema.additionalPropertiesSchema, isRequest)
            return DeclaredType("java.util.Map", keyType, valueType)
        }

        val modelName = if (isRequest) {
            modelNamingStrategy.createRequestModelName(HttpMethod.POST, schema)
        } else {
            modelNamingStrategy.createResponseModelName(schema)
        }

        //val modelName = modelNamingStrategy.createModelName(schema)
        return DeclaredType("${modelPackageName}.${modelName}")
    }

    override fun isPlatformType(type: Type): Boolean {
        if (type.getKind() != TypeKind.DECLARED) {
            return true
        } else {
            val declaredType = type as DeclaredType
            return declaredType.getQualifiedName().startsWith("java.")
        }
    }

    override fun isOpenApiType(schemaName: String): Boolean {
        if (OpenApiType.OBJECT.type == schemaName) {
            return false
        } else {
            return OpenApiType.isTypeOpenApiType(schemaName)
        }
    }

    override fun isOpenApiType(schema: Schema): Boolean {
        val type = schema.type

        if (OpenApiType.ARRAY.type == type) {
            return isOpenApiType(schema.itemsSchema)
        } else {
            return isOpenApiType(type)
        }
    }

    override fun unbox(primitiveType: PrimitiveType): Type {
        return when(primitiveType.getKind()) {
            TypeKind.BOOLEAN -> types.getBoolean(true)
            TypeKind.SHORT -> types.getShort(true)
            TypeKind.CHAR -> types.getChar(true)
            TypeKind.INT -> types.getInteger(true)
            TypeKind.LONG -> types.getLong(true)
            TypeKind.FLOAT -> types.getFloat(true)
            TypeKind.DOUBLE -> types.getDouble(true)
            else -> primitiveType
        }
    }
}