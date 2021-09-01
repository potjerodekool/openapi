package org.platonos.rest.generate.openapi

import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import com.reprezen.kaizen.oasparser.model3.Schema

class TypesCreatorJavaImpl: TypesCreator {

    override fun createType(schema: Schema): Type {
        val format = schema.format

        return when(OpenApiType.fromType(schema.type)) {
            OpenApiType.STRING -> {
                if (format == null) {
                    createClassType("java.lang.String")
                } else if (format == "date") {
                    createClassType("java.util.LocalDate")
                } else {
                    TODO()
                }
            }
            else -> TODO()
        }
    }

    private fun createClassType(className: String): ClassOrInterfaceType {
        return ClassOrInterfaceType(null, className)
    }
}