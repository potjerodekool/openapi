package org.platonos.rest.generate.openapi.generator

import com.google.googlejavaformat.java.Formatter
import org.platonos.rest.generate.Templates
import org.platonos.rest.generate.element.CompilationUnit
import org.platonos.rest.generate.element.PackageElement
import org.platonos.rest.generate.element.TypeElement
import org.platonos.rest.generate.openapi.FileWriter
import org.platonos.rest.generate.openapi.ImportOrganiser
import org.platonos.rest.generate.openapi.PlatformSupport
import org.platonos.rest.generate.util.LogLevel
import org.platonos.rest.generate.util.Logger
import java.io.File

class Filer(private val sourceDir: File,
            private val platformSupport: PlatformSupport) {

    private val logger = Logger.getLogger(Filer::class.java)

    fun createSource(typeElement: TypeElement) {
        val packageElement = typeElement.enclosingElement as PackageElement
        val importOrganiser = ImportOrganiser()
        CompilationUnit(packageElement, typeElement).accept(importOrganiser, null)
        val newCu = importOrganiser.getCompilationUnit()
        generateCode(newCu, sourceDir, platformSupport)
    }

    private fun generateCode(compilationUnit: CompilationUnit, sourceDir: File, platformSupport: PlatformSupport) {
        val template = Templates.getInstanceOf("class/compilationUnit")!!
        template.add("compilationUnit", compilationUnit)
        val code = template.render()
        val formattedCode = Formatter().formatSource(code)

        val packageName = compilationUnit.packageElement.getQualifiedName()

        val outputFile = FileWriter.write(
            sourceDir,
            packageName,
            compilationUnit.typeElement.simpleName,
            platformSupport.getSourceFileExtension(),
            formattedCode)

        logger.log(LogLevel.INFO, "Generated source file ${outputFile.absoluteFile}")
    }
}