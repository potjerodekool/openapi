package org.platonos.rest.generate.openapi

import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.generate.Build
import org.platonos.rest.generate.openapi.generator.Filer
import org.platonos.rest.generate.openapi.generator.OpenApiModelGenerator
import org.platonos.rest.generate.openapi.generator.api.ApisGeneratorFactory
import org.platonos.rest.generate.openapi.resolver.IdProperty

class OpenApiGenerator {

    private lateinit var openApiModelGenerator: OpenApiModelGenerator

    fun generate(build: Build,
                 config: OpenApiGeneratorConfiguration,
                 platformSupport: PlatformSupport,
                 openApi: OpenApi3,
                 schemas: Map<String, Schema>,
                 patchSchemas: Map<String, Schema>,
                 idSchemas: Map<String, IdProperty>) {
        val filer = Filer(build.sourceDir, platformSupport)

        openApiModelGenerator = OpenApiModelGenerator(config, platformSupport, filer)


        if (config.generateModels) {
            generateModels(openApi, schemas, patchSchemas)
        }

        if (config.generateApiDefinitions || config.generateApiImplementations) {
            generateApis(openApi, config, platformSupport, build, filer, idSchemas)
        }
    }

    private fun generateModels(
        openApi: OpenApi3,
        schemas: Map<String, Schema>,
        patchSchemas: Map<String, Schema>) {
        openApiModelGenerator.generateModels(
            openApi,
            schemas,
            patchSchemas
        )
    }

    private fun generateApis(
        openApi: OpenApi3,
        config: OpenApiGeneratorConfiguration,
        platformSupport: PlatformSupport,
        build: Build,
        filer: Filer,
        idSchemas: Map<String, IdProperty>
    ) {
        val apisGenerator = ApisGeneratorFactory.createApisGenerator(config.generator)
        apisGenerator.generateApis(openApi, config, platformSupport, build.sourceDir, filer, idSchemas)
    }
}