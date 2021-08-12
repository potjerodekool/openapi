package org.platonos.rest.gen.openapi.generator

import com.google.googlejavaformat.java.Formatter
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import org.platonos.rest.gen.Templates
import org.platonos.rest.gen.element.CompilationUnit
import org.platonos.rest.gen.element.PackageElement
import org.platonos.rest.gen.openapi.*
import org.platonos.rest.gen.openapi.generator.api.ApiDefinitionGenerator
import org.platonos.rest.gen.openapi.generator.api.ApiGeneratorFactory
import org.platonos.rest.gen.openapi.generator.api.ApiImplementationGenerator
import org.platonos.rest.gen.util.LogLevel
import org.platonos.rest.gen.util.Logger
import java.io.File
import java.util.*

class ApisGenerator {

    private val logger = Logger.getLogger(javaClass)

    fun generateApis(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        sourceDir: File
    ) {
        val packageElement = PackageElement(config.apiPackageName)
        generateApiDefinitions(openApi, config, platformSupport, sourceDir, packageElement)
        generateApiImplementations(openApi, config, platformSupport, sourceDir, packageElement)
        generateUtils(config, sourceDir)
    }

    private fun generateApiDefinitions(openApi: OpenApi3,
                                       config: OpenApiGeneratorConfiguration,
                                       platformSupport: PlatformSupport,
                                       sourceDir: File,
                                       packageElement: PackageElement) {
        val apiDefinitionGenerators = mutableMapOf<String, ApiDefinitionGenerator>()

        if (config.generateApiDefintions) {
            openApi.paths.forEach { (url, path) ->
                val controllerKey = createControllerKey(url)

                val generator = apiDefinitionGenerators.computeIfAbsent(controllerKey) {
                    val newGenerator = ApiGeneratorFactory.createApiDefinitionGenerator(config.generator)
                    newGenerator.init(config, platformSupport, controllerKey, packageElement)
                    newGenerator
                }

                if (path != null) {
                    generator.generateApiDefinition(url, path)
                }
            }

            apiDefinitionGenerators.values.forEach { apiGenerator ->
                val apiDefinition = apiGenerator.getApiDefinition()
                val importOrganiser = ImportOrganiser()
                CompilationUnit(packageElement, apiDefinition).accept(importOrganiser, null)
                generateCode(importOrganiser.getCompilationUnit(), sourceDir, platformSupport)
            }
        }
    }


    private fun generateApiImplementations(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        sourceDir: File,
        packageElement: PackageElement
    ) {
        val apiImplementationGenerators = mutableMapOf<String, ApiImplementationGenerator>()

        if (config.generateApiImplementations) {
            openApi.paths.forEach { (url, path) ->
                val controllerKey = createControllerKey(url)

                val generator = apiImplementationGenerators.computeIfAbsent(controllerKey) {
                    val newGenerator = ApiGeneratorFactory.createApiImplementationGenerator(config.generator)
                    newGenerator.init(config, platformSupport, controllerKey, packageElement)
                    newGenerator
                }

                if (path != null) {
                    generator.generateApiImplementation(url, path)
                }
            }

            apiImplementationGenerators.values.forEach { apiGenerator ->
                val apiImplementation = apiGenerator.getApiImplementation()
                val importOrganiser = ImportOrganiser()
                CompilationUnit(packageElement, apiImplementation).accept(importOrganiser, null)
                generateCode(importOrganiser.getCompilationUnit(), sourceDir, platformSupport)
            }
        }
    }

    private fun createControllerKey(url: String): String {
        val key = StringJoiner("/")
        val parts = url.split("/")
        var index = parts.size - 1
        var stopIndex = -1

        do {
            val part = parts[index]

            if (stopIndex == -1) {
                if (isPathVariable(part).not()) {
                    stopIndex = index
                }
            }
            index--
        } while (index > 0)

        index = 0

        do {
            val part = parts[index]

            if (isPathVariable(part).not() && part.isNotEmpty()) {
                key.add(part)
            }
            index++
        } while (index <= stopIndex)

        return key.toString()
    }

    private fun isPathVariable(pathElement: String): Boolean {
        return pathElement.startsWith("{")
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

    private fun generateUtils(config: OpenApiGeneratorConfiguration, sourceDir: File) {

        val generator = ApiGeneratorFactory.createUtilsGenerator(config.generator)
        val utilsCode = generator.generateUtils(config.apiPackageName)

        FileWriter.write(
            sourceDir,
            config.apiPackageName,
            "ApiUtils",
            "java",
            utilsCode
        )
    }

}