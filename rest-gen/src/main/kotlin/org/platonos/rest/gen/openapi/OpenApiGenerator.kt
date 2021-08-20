package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.OpenApiParser
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.openapi.generator.Filer
import org.platonos.rest.gen.openapi.generator.OpenApiModelGenerator
import org.platonos.rest.gen.openapi.generator.api.ApisGeneratorFactory
import org.platonos.rest.gen.openapi.resolver.IdProperty
import org.platonos.rest.gen.openapi.resolver.SchemasResolver
import java.io.File

class OpenApiGenerator {

    private val parser = OpenApiParser()
    private lateinit var openApiModelGenerator: OpenApiModelGenerator

    fun generate(options: Options,
                 build: Build) {
        val openApi = OpenApiMerger().merge(File(options.fileName))
            //parse(options.fileName)
        val config = OpenApiGeneratorConfigurationBuilder()
            .createConfig(options)

        val schemasResolver = SchemasResolver(config)
        schemasResolver.visitOpenApi(openApi)

        val schemas = schemasResolver.getSchemas()
        val patchSchemas = schemasResolver.getPatchSchemas()
        val idSchemas = schemasResolver.getIdSchemas();

        val platformSupport = PlatformSupportJava(
            config.modelNamingStrategy,
            config.modelPackageName
        )

        openApiModelGenerator = OpenApiModelGenerator(config, platformSupport)

        if (config.generateModels) {
            generateModels(schemas, patchSchemas, config, build)
        }

        if (config.generateApiDefintions || config.generateApiImplementations) {
            val filer = Filer(build.sourceDir, platformSupport)
            generateApis(openApi, config, platformSupport, build, filer, idSchemas)
        }
    }

    private fun parse(fileName: String): OpenApi3 {
        val file = File(fileName)
        return parser.parse(file) as OpenApi3
    }

    private fun generateModels(schemas: Map<String, Schema>,
                               patchSchemas: Map<String, Schema>,
                               config: OpenApiGeneratorConfiguration,
                               build: Build) {
        openApiModelGenerator.generateModels(
            schemas,
            patchSchemas,
            config,
            build.sourceDir
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