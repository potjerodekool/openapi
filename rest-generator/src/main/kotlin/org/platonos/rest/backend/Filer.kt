package org.platonos.rest.backend

import com.google.googlejavaformat.java.Formatter
import org.platonos.rest.generate.Templates
import org.platonos.rest.generate.element.CompilationUnit
import org.platonos.rest.generate.element.PackageElement
import org.platonos.rest.generate.element.TypeElement
import org.platonos.rest.openapi.FileWriter
import org.platonos.rest.openapi.ImportOrganiser
import org.platonos.rest.openapi.PlatformSupport
import org.platonos.rest.generate2.util.LogLevel
import org.platonos.rest.generate2.util.Logger
import java.io.File
import java.lang.Exception

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

    fun createSource(packageName: String,
                     simpleName: String,
                     source: String) {
        writeCode(packageName, simpleName, source)
    }

    private fun generateCode(compilationUnit: CompilationUnit, sourceDir: File, platformSupport: PlatformSupport) {
        val template = Templates.getInstanceOf("class/compilationUnit")!!
        template.add("compilationUnit", compilationUnit)
        val code = template.render()

        var formattedCode: String

        try {
            formattedCode = Formatter().formatSource(code)
        } catch (e: Exception) {
            formattedCode = code
        } catch (e: IllegalAccessError) {
            //Fails on JDK 17
            formattedCode = code
        }

        val packageName = compilationUnit.packageElement.getQualifiedName()

        writeCode(
            packageName,
            compilationUnit.typeElement.simpleName,
            formattedCode
        )
    }

    private fun writeCode(packageName: String,
                          simpleName: String,
                          code: String) {
        val outputFile = FileWriter.write(
            sourceDir,
            packageName,
            simpleName,
            platformSupport.getSourceFileExtension(),
            code
        )

        logger.log(LogLevel.INFO, "Generated source file ${outputFile.absoluteFile}")
    }
}