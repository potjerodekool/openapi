package org.platonos.rest.gen.openapi.generator

import com.google.googlejavaformat.java.Formatter
import org.platonos.rest.gen.Templates
import org.platonos.rest.gen.element.CompilationUnit
import org.platonos.rest.gen.element.PackageElement
import org.platonos.rest.gen.element.TypeElement
import org.platonos.rest.gen.openapi.FileWriter
import org.platonos.rest.gen.openapi.ImportOrganiser
import org.platonos.rest.gen.openapi.PlatformSupport
import org.platonos.rest.gen.util.LogLevel
import org.platonos.rest.gen.util.Logger
import java.io.File

class Filer(private val sourceDir: File,
            private val platformSupport: PlatformSupport) {

    private val logger = Logger.getLogger(Filer::class.java)

    fun createSource(typeElement: TypeElement) {
        val packageElement = typeElement.enclosingElement as PackageElement
        val importOrganiser = ImportOrganiser()
        CompilationUnit(packageElement, typeElement).accept(importOrganiser, null)
        generateCode(importOrganiser.getCompilationUnit(), sourceDir, platformSupport)
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