package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.type.DeclaredType
import org.platonos.rest.gen.type.Type
import org.platonos.rest.gen.type.TypeKind
import java.time.LocalDate
import java.util.*

class JavaTypeConverter(
    private val types: TypesJava,
    private val modelNamingStrategy: ModelNamingStrategy,
    private val modelPackageName: String
) : TypeConverter {

    override fun convert(type: String,
                         schema: Schema): Type {
        val schemaFormat = schema.format

        return when(OpenApiType.fromType(type)) {
            OpenApiType.INTEGER -> {
                if (schemaFormat == "int32") {
                    return types.getInteger(schema.isNullable)
                } else if (schemaFormat == "int64") {
                    return types.getInteger(schema.isNullable)
                } else {
                    throw OpenApiException("Schema with type integer should have format int32 or int64 but was $schemaFormat")
                }
            }
            OpenApiType.NUMBER -> {
                if (schemaFormat == "float") {
                    types.getFloat(schema.isNullable)
                } else if (schemaFormat == "double") {
                    types.getDouble(schema.isNullable)
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
                    types.getString(schema.isNullable)
                }
            }
            OpenApiType.BOOLEAN -> types.getBoolean(schema.isNullable)
            OpenApiType.ARRAY -> {
                val itemsSchema = schema.itemsSchema
                DeclaredType(
                    "java.util.List",
                    listOf(convert(itemsSchema.type, itemsSchema))
                )
            }
            OpenApiType.OBJECT -> {
                val modelName = modelNamingStrategy.createModelName(schema)
                DeclaredType("${modelPackageName}.${modelName}")
            }
        }
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
        return OpenApiType.isTypeOpenApiType(schemaName)
    }

    override fun isOpenApiType(schema: Schema): Boolean {
        return isOpenApiType(schema.type)
    }
}