package org.platonos.rest.gen.openapi.generator

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfigurationBuilder
import org.platonos.rest.gen.openapi.Options
import org.platonos.rest.gen.openapi.PlatformSupportJava
import org.platonos.rest.gen.openapi.generator.api.spring.ApisGeneratorSpring
import org.platonos.rest.gen.openapi.resolver.SchemasResolver
import java.io.File

internal class ApisGeneratorTest {

    private val apisGenerator = ApisGeneratorSpring()

    val dir = File("target/gen")

    @AfterEach
    fun tearDown() {
        deleteDir(dir)
    }

    @Test
    fun generatePaths() {
        val options = Options(
            fileName = "C:\\projects\\rest-dto\\demo\\openapi\\spec.yml",
            modelPackageName = "org.platonos.demo.api.model",
            apiPackageName = "org.platonos.demo.api",
            generateApiDefintions = true,
            generateApiImplementations = true
        )

        val openApi = Parser.parse(options.fileName)

        val config = OpenApiGeneratorConfigurationBuilder()
            .createConfig(options)

        val platformSupport = PlatformSupportJava(config.modelNamingStrategy, config.modelPackageName)

        val schemasResolver = SchemasResolver(config)
        schemasResolver.visitOpenApi(openApi)
        val idSchemas = schemasResolver.getIdSchemas();

        val filer = Filer(dir, platformSupport)

       apisGenerator.generateApis(
           openApi,
           config,
           platformSupport,
           dir,
           filer,
           idSchemas
       )
    }

    private fun deleteDir(dir: File) {
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }
}