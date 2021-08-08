package org.platonos.rest.gen.openapi.generator

import com.reprezen.kaizen.oasparser.model3.OpenApi3
import org.platonos.rest.gen.Templates
import org.platonos.rest.gen.element.CompilationUnit
import org.platonos.rest.gen.element.ElementKind
import org.platonos.rest.gen.element.MethodElement
import org.platonos.rest.gen.element.PackageElement
import org.platonos.rest.gen.openapi.*
import org.platonos.rest.gen.openapi.generator.api.ApiGenerator
import org.platonos.rest.gen.openapi.generator.api.ApiGeneratorFactory
import org.platonos.rest.gen.util.LogLevel
import org.platonos.rest.gen.util.Logger
import java.io.File

class ApisGenerator {

    private val logger = Logger.getLogger(javaClass)

    fun generateApis(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration,
        options: Options,
        platformSupport: PlatformSupport,
        sourceDir: File
    ) {
        val packageElement = PackageElement(options.apiPackage)

        val apiGenerators = mutableMapOf<String, ApiGenerator>()

        openApi.paths.forEach { (url, path) ->
            val controllerKey = createControllerKey(url)

            val generator = apiGenerators.computeIfAbsent(controllerKey) {
                val newGenerator = ApiGeneratorFactory.createGenerator(options.generator)
                newGenerator.init(config, options,platformSupport, controllerKey, packageElement)
                newGenerator
            }

            if (path != null) {
                generator.generate(url, path)
            }
        }

        apiGenerators.values.forEach { apiGenerator ->
            val typeElement = apiGenerator.getTypeElement()
            val importOrganiser = ImportOrganiser()
            CompilationUnit(packageElement, typeElement).accept(importOrganiser, null)
            generateCode(importOrganiser.getCompilationUnit(), sourceDir, platformSupport)
        }

    }

    private fun createControllerKey(url: String): String {
        val key = StringBuilder()
        val parts = url.split("/")
        var index = 0
        var process = true

        do {
            val part = parts[index]

            if (part == "") {
                key.append("/")
            } else if (part.startsWith("{")) {
                process = false
            } else {
                key.append(part)
            }
            index++
        } while (index < parts.size && process)

        return key.toString()
    }

    private fun generateCode(compilationUnit: CompilationUnit, sourceDir: File, platformSupport: PlatformSupport) {
        val template = Templates.getInstanceOf("class/compilationUnit")!!
        template.add("compilationUnit", compilationUnit)
        val code = template.render()

        val packageName = compilationUnit.packageElement.getQualifiedName()

        val outputFile = FileWriter.write(
            sourceDir,
            packageName,
            compilationUnit.typeElement.simpleName,
            platformSupport.getSourceFileExtension(),
            code)

        logger.log(LogLevel.INFO, "Generated source file ${outputFile.absoluteFile}")
    }

}