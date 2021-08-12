package org.platonos.rest.gen.openapi.generator

import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.Templates
import org.platonos.rest.gen.element.CompilationUnit
import org.platonos.rest.gen.openapi.*
import org.platonos.rest.gen.openapi.generator.model.DefaultModelBuilder
import org.platonos.rest.gen.openapi.generator.model.PatchModelBuilder
import org.platonos.rest.gen.openapi.generator.model.SourcePath
import org.platonos.rest.gen.util.LogLevel
import org.platonos.rest.gen.util.Logger
import java.io.File

class OpenApiModelGenerator(private val config: OpenApiGeneratorConfiguration,
                            private val platformSupport: PlatformSupportJava) {

    private val logger = Logger.getLogger(javaClass)

    fun generateModels(schemas: Map<String, Schema>,
                       patchSchemas: Map<String, Schema>,
                       config: OpenApiGeneratorConfiguration,
                       targetDir: File) {

        val packageName = config.modelPackageName
        val compilationUnits = generateCompilationUnits(schemas, patchSchemas, config)
        val sourceFileExtension = platformSupport.getSourceFileExtension()

        val newCompilationUnits = organizeImports(compilationUnits)

        newCompilationUnits.forEach { compilationUnit ->
            val code = generateCode(compilationUnit)
            val outputFile = FileWriter.write(
                targetDir,
                packageName,
                compilationUnit.typeElement.simpleName,
                sourceFileExtension,
                code)
            logger.log(LogLevel.INFO, "Generated source file ${outputFile.absoluteFile}")
        }
    }

    private fun organizeImports(compilationUnits: Collection<CompilationUnit>): Collection<CompilationUnit> {
        val newCompilationUnits = mutableListOf<CompilationUnit>()

        compilationUnits.forEach { compilationUnit ->
            val importOrganiser = ImportOrganiser()
            compilationUnit.accept(importOrganiser, null)
            newCompilationUnits.add(importOrganiser.getCompilationUnit())
        }

        return newCompilationUnits
    }

    private fun generateCompilationUnits(schemas: Map<String, Schema>,
                                         patchSchemas: Map<String, Schema>,
                                         config: OpenApiGeneratorConfiguration): Collection<CompilationUnit> {
        val packageName = config.modelPackageName
        val sourcePath = SourcePath(schemas)
        val dynamicModels = config.dynamicModels

        schemas
            .filterNot { dynamicModels.contains(it.key) }
            .forEach { ( modelName, schema ) ->
                val modelBuilder = DefaultModelBuilder(platformSupport, config, packageName, sourcePath)
                modelBuilder.buildModel(modelName, schema)
        }

        patchSchemas
            .filterNot { dynamicModels.contains(it.key) }
            .forEach { (modelName, schema) ->
                val patchModelBuilder = PatchModelBuilder(platformSupport, config, packageName, sourcePath)
                patchModelBuilder.buildModel(modelName, schema)
        }

        return sourcePath.getCompilationUnits()
    }

    private fun generateCode(compilationUnit: CompilationUnit): String {
        val st = Templates.getInstanceOf("class/compilationUnit")!!
        st.add("compilationUnit", compilationUnit)
        return st.render()
    }

}