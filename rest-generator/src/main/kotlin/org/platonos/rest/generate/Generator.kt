package org.platonos.rest.generate

import com.github.javaparser.ast.CompilationUnit
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.mapper.JavaParser
import org.platonos.rest.generate.mapper.MapperGenerator
import org.platonos.rest.generate.openapi.*
import org.platonos.rest.generate.openapi.resolver.IdProperty
import org.platonos.rest.generate.openapi.resolver.SchemasResolver
import java.io.File
import java.net.URL
import java.net.URLClassLoader

class Generator {

    fun generate(options: Options,
                 projectInfo: ProjectInfo) {
        val openApi = OpenApiMerger().merge(File(options.fileName))
        val config = OpenApiGeneratorConfigurationBuilder()
            .createConfig(options)

        val schemasResolver = SchemasResolver(config)
        schemasResolver.visitOpenApi(openApi)

        val schemas = schemasResolver.getSchemas()
        val patchSchemas = schemasResolver.getPatchSchemas()
        val idSchemas = schemasResolver.getIdSchemas()

        val platformSupport = PlatformSupportJava(
            config.modelNamingStrategy,
            config.modelPackageName
        )

        val build = projectInfo.build
        generateApis(build, config, platformSupport, openApi, schemas, patchSchemas, idSchemas)
        //generateMappers(projectInfo, config)
    }

    private fun generateApis(build: Build,
                             config: OpenApiGeneratorConfiguration,
                             platformSupport: PlatformSupport,
                             openApi: OpenApi3,
                             schemas: Map<String, Schema>,
                             patchSchemas: Map<String, Schema>,
                             idSchemas: Map<String, IdProperty>) {
        val apiGenerator = OpenApiGenerator()
        apiGenerator.generate(build, config, platformSupport, openApi, schemas, patchSchemas, idSchemas)
    }

    private fun generateMappers(
        projectInfo: ProjectInfo,
        config: OpenApiGeneratorConfiguration)
    {
        val build = projectInfo.build

        val urls = projectInfo.classPath
            .map { fileName -> toUrl(fileName) }
            .toTypedArray()

        val javaParser = JavaParser(URLClassLoader(urls))

        val modelDir = File(build.sourceDir, config.modelPackageName.replace('.', File.separatorChar))

        val models = mutableListOf<CompilationUnit>()
        val entities = mutableListOf<CompilationUnit>()

        loadModels(modelDir, models, javaParser)

        projectInfo.sourceRoots.forEach { sourceRoot ->
            scanDirectory(File(sourceRoot), entities, javaParser)
        }

        val mapperGenerator = MapperGenerator()
        mapperGenerator.generate(models, entities)
    }

    private fun toUrl(fileName: String): URL {
        return File(fileName).toURI().toURL()
    }

    private fun loadModels(dir: File,
                           compilationUnits: MutableList<CompilationUnit>,
                           javaParser: JavaParser) {
        val files = dir.listFiles()

        if (files != null) {
            files.forEach { file ->
                if (file.isFile && file.extension == "java") {
                    val compilationUnit = javaParser.parse(file)

                    if (compilationUnit != null) {
                        compilationUnits += compilationUnit
                    }
                }
            }
        }
    }

    private fun scanDirectory(dir: File, compilationUnits: MutableList<CompilationUnit>, javaParser: JavaParser) {
        val files = dir.listFiles()

        if (files != null) {
            files.forEach { file ->
                if (file.isDirectory) {
                    scanDirectory(file, compilationUnits, javaParser)
                } else if (file.isFile && file.extension == "java") {
                    val compilationUnit = javaParser.parse(file)

                    if (compilationUnit != null) {
                        if (isJpaModel(compilationUnit)) {
                            compilationUnits += compilationUnit
                        }
                    }
                }
            }
        }
    }

    private fun isJpaModel(compilationUnit: CompilationUnit): Boolean {
        val types = compilationUnit.types

        if (types.size != 1) {
            return false
        }

        val typeElement = types.first.get()
        try {
            val resoledElement = typeElement.resolve()
            return resoledElement.hasAnnotation("javax.persistence.Entity")
        } catch (e: Exception) {
            return false
        }
    }

}