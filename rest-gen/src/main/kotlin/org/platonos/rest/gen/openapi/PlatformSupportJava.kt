package org.platonos.rest.gen.openapi

import org.platonos.rest.gen.type.Type
import org.platonos.rest.gen.type.TypeKind

class PlatformSupportJava(modelNamingStrategy: ModelNamingStrategy,
                          modelPackageName: String) : PlatformSupport {

    private val types = TypesJava()

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