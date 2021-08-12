package org.platonos.rest.gen.openapi

import com.reprezen.kaizen.oasparser.OpenApiParser
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema
import org.platonos.rest.gen.openapi.generator.ApisGenerator
import org.platonos.rest.gen.openapi.generator.OpenApiModelGenerator
import org.platonos.rest.gen.util.Logger
import java.io.File

class OpenApiGenerator {

    private val logger = Logger.getLogger(OpenApiGenerator::class.java)

    private val parser = OpenApiParser()
    private lateinit var openApiModelGenerator: OpenApiModelGenerator
    private val apisGenerator = ApisGenerator()

    fun generate(options: Options,
                 build: Build) {
        val openApi = parse(options.fileName)
        val config = OpenApiGeneratorConfigurationBuilder()
            .createConfig(options)

        val schemasResolver = SchemasResolver(config)
        schemasResolver.visitOpenApi(openApi)
        val schemas = schemasResolver.getSchemas()
        val patchSchemas = schemasResolver.getPatchSchemas()

        val platformSupport = PlatformSupportJava()

        openApiModelGenerator = OpenApiModelGenerator(config, platformSupport)

        if (config.generateModels) {
            generateModels(schemas, patchSchemas, config, build)
        }

        if (config.generateApiDefintions || config.generateApiImplementations) {
            generateApis(openApi, config, platformSupport, build)
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

    private fun generateApis(openApi: OpenApi3,
                             config: OpenApiGeneratorConfiguration,
                             platformSupport: PlatformSupport,
                             build: Build) {
        apisGenerator.generateApis(openApi, config, platformSupport, build.sourceDir)
    }
}