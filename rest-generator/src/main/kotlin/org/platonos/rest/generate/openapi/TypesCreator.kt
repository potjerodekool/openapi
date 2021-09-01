package org.platonos.rest.generate.openapi

import com.github.javaparser.ast.type.Type
import com.reprezen.kaizen.oasparser.model3.Schema

interface TypesCreator {

    fun createType(schema: Schema): Type
}