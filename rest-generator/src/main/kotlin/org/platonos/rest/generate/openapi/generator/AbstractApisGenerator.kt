package org.platonos.rest.generate.openapi.generator

import com.google.googlejavaformat.java.Formatter
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import org.platonos.rest.generate.Templates
import org.platonos.rest.generate.element.CompilationUnit
import org.platonos.rest.generate.element.PackageElement
import org.platonos.rest.generate.openapi.*
import org.platonos.rest.generate.openapi.generator.api.*
import org.platonos.rest.generate.openapi.resolver.IdProperty
import org.platonos.rest.generate.util.LogLevel
import org.platonos.rest.generate.util.Logger
import java.io.File

abstract class AbstractApisGenerator : ApisGenerator {

    private val logger = Logger.getLogger(javaClass)

    override fun generateApis(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        sourceDir: File,
        filer: Filer,
        idSchemas: Map<String, IdProperty>
    ) {
        val packageElement = PackageElement(config.apiPackageName)

        generateApiDefinitions(openApi, config, platformSupport, sourceDir, packageElement, filer)
        generateApiImplementations(openApi, config, platformSupport, packageElement, filer, idSchemas)
        generateUtils(config, sourceDir)
    }

    private fun generateApiDefinitions(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        sourceDir: File,
        packageElement: PackageElement,
        filer: Filer) {
        val apiDefinitionGenerators = mutableMapOf<String, ApiDefinitionGenerator>()

        if (config.generateApiDefinitions) {
            openApi.paths.forEach { (url, path) ->
                val controllerKey = createControllerKey(url)

                val generator = apiDefinitionGenerators.computeIfAbsent(controllerKey) {
                    val newGenerator = createApiDefinitionGenerator()
                    newGenerator.init(config, platformSupport, controllerKey, packageElement, filer)
                    newGenerator
                }

                if (path != null) {
                    generator.generate(url, path)
                }
            }

            apiDefinitionGenerators.values.forEach { apiGenerator ->
                apiGenerator.finish()
                //val apiDefinition = apiGenerator.getApiElement()
                //val importOrganiser = ImportOrganiser()
                //CompilationUnit(packageElement, apiDefinition).accept(importOrganiser, null)
                //generateCode(importOrganiser.getCompilationUnit(), sourceDir, platformSupport)
            }
        }
    }

    abstract fun createApiDefinitionGenerator(): ApiDefinitionGenerator

    private fun generateApiImplementations(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        packageElement: PackageElement,
        filer: Filer,
        idSchemas: Map<String, IdProperty>)
    {
        val apiImplementationGenerators = mutableMapOf<String, ApiImplementationGenerator>()

        if (config.generateApiImplementations) {
            openApi.paths.forEach { (url, path) ->
                val controllerKey = createControllerKey(url)

                val generator = apiImplementationGenerators.computeIfAbsent(controllerKey) {
                    val newGenerator = createApiImplementationGenerator()
                    newGenerator.init(config, platformSupport, controllerKey, packageElement, filer, idSchemas)
                    newGenerator
                }

                if (path != null) {
                    generator.generate(url, path)
                }
            }

            apiImplementationGenerators.values.forEach { apiGenerator ->
                apiGenerator.finish()
                //val apiImplementation = apiGenerator.getApiElement()
                //val importOrganiser = ImportOrganiser()
                //CompilationUnit(packageElement, apiImplementation).accept(importOrganiser, null)
                //generateCode(importOrganiser.getCompilationUnit(), sourceDir, platformSupport)
            }
        }
    }

    abstract fun createApiImplementationGenerator(): ApiImplementationGenerator

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

        val generator = createUtilsGenerator()
        val utilsCode = generator.generateUtils(config.apiPackageName)

        FileWriter.write(
            sourceDir,
            config.apiPackageName,
            "ApiUtils",
            "java",
            utilsCode
        )
    }

    abstract fun createUtilsGenerator(): UtilsGenerator

}