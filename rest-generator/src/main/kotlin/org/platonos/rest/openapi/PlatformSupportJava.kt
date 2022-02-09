package org.platonos.rest.openapi

import org.platonos.rest.generate.type.Type
import org.platonos.rest.generate.type.TypeKind

class PlatformSupportJava(
    private val modelNamingStrategy: ModelNamingStrategy,
    modelPackageName: String) : PlatformSupport {

    private val types = TypesJava()

    override fun getModelNamingStrategy(): ModelNamingStrategy {
        return modelNamingStrategy
    }

    private val typeConverter = JavaTypeConverter(types, modelNamingStrategy, modelPackageName)

    override fun getTypeConverter(): TypeConverter {
        return typeConverter
    }

    override fun getSourceFileExtension(): String {
        return "java"
    }

    override fun isPlatformType(type: Type): Boolean {
        if (type.getKind() != TypeKind.DECLARED) {
            return true
        } else {
            return typeConverter.isPlatformType(type)
        }
    }
}