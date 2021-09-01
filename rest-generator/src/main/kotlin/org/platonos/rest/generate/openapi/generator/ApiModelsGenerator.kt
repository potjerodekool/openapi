package org.platonos.rest.generate.openapi.generator

import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.element.CompilationUnit
import org.platonos.rest.generate.openapi.*
import org.platonos.rest.generate.openapi.api.ContentType
import org.platonos.rest.generate.openapi.api.HttpMethod
import org.platonos.rest.generate.openapi.generator.model.AbstractModelBuilder
import org.platonos.rest.generate.openapi.generator.model.DefaultModelBuilder
import org.platonos.rest.generate.openapi.generator.model.PatchModelBuilder
import org.platonos.rest.generate.openapi.generator.model.SourcePath
import org.platonos.rest.generate.util.Logger

class OpenApiModelGenerator(private val config: OpenApiGeneratorConfiguration,
                            private val platformSupport: PlatformSupport,
                            private val filer: Filer): OpenApiVisitor {

    private val logger = Logger.getLogger(javaClass)
    private lateinit var sourcePath: SourcePath

    fun generateModels(
        openApi: OpenApi3,
        schemas: Map<String, Schema>,
        patchSchemas: Map<String, Schema>)
    {
        sourcePath = SourcePath(schemas)

        /*
        val compilationUnits = generateCompilationUnits(schemas, patchSchemas, config)

        compilationUnits.forEach { cu ->
            filer.createSource(cu.typeElement)
        }

         */

        visitOpenApi(openApi)
    }

    private fun generateCompilationUnits(
        schemas: Map<String, Schema>,
        patchSchemas: Map<String, Schema>,
        config: OpenApiGeneratorConfiguration,
    ): Collection<CompilationUnit> {
        sourcePath = SourcePath(schemas)
        val dynamicModels = config.dynamicModels

        schemas
            .filterNot { dynamicModels.contains(it.key) }
            .forEach { ( modelName, schema ) ->
                val modelBuilder = DefaultModelBuilder(platformSupport, config, sourcePath, false)
                modelBuilder.buildModel(modelName, schema)
        }

        patchSchemas
            .filterNot { dynamicModels.contains(it.key) }
            .forEach { (modelName, schema) ->
                val patchModelBuilder = PatchModelBuilder(platformSupport, config, sourcePath)
                patchModelBuilder.buildModel(modelName, schema)
        }

        return sourcePath.getCompilationUnits()
    }

    override fun visitOperation(method: HttpMethod, requestPath: String, operation: Operation) {
        if (operation.requestBody != null) {
            val contentMediaType = operation.requestBody.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)

            if (contentMediaType != null) {
                val modelNamingStrategy = config.modelNamingStrategy

                val schema = contentMediaType.schema
                val modelBuilder: AbstractModelBuilder
                val modelName: String

                if (method == HttpMethod.PATCH) {
                    modelBuilder = PatchModelBuilder(
                        platformSupport,
                        config,
                        sourcePath
                    )
                    modelName = modelNamingStrategy.createPatchModelName(schema)
                } else {
                    modelBuilder = DefaultModelBuilder(
                        platformSupport,
                        config,
                        sourcePath,
                        false
                    )

                    modelName = modelNamingStrategy.createModelName(schema)
                }

                val model = modelBuilder.buildModel(modelName, schema)
                filer.createSource(model)
            }
        }

        operation.responses.forEach { (_, response) ->
            val contentMediaType = response.getContentMediaType(ContentType.APPLICATION_JSON.descriptor)

            if (contentMediaType != null) {
                val schema = contentMediaType.schema

                val builder = DefaultModelBuilder(
                    platformSupport,
                    config,
                    sourcePath,
                    true
                )

                val modelNamingStrategy = config.modelNamingStrategy
                val modelName = modelNamingStrategy.createModelName(schema)

                val model = builder.buildModel(modelName, schema)
                filer.createSource(model)
            }
        }
    }

}