package org.platonos.rest.generate.openapi.generator.model

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.element.CompilationUnit
import org.platonos.rest.generate.element.PackageElement
import org.platonos.rest.generate.element.TypeElement

class SourcePath(private val schemas: Map<String, Schema>) {

    private val sourceElements = mutableMapOf<String, TypeElement>()

    private val compilationUnits = mutableListOf<CompilationUnit>()

    fun addTypeElement(typeElement: TypeElement) {
        sourceElements[typeElement.getQualifiedName()] = typeElement

        val pck = typeElement.enclosingElement as PackageElement
        val compilationUnit = CompilationUnit(pck, typeElement)
        compilationUnits += compilationUnit
    }

    fun typeElementExists(qualifiedName: String): Boolean {
        return sourceElements.containsKey(qualifiedName)
    }

    fun getSchema(name: String): Schema? {
        return schemas[name]
    }


    fun getCompilationUnits(): List<CompilationUnit> {
        return compilationUnits
    }
}