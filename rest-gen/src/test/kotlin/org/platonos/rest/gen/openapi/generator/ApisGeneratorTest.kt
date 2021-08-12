package org.platonos.rest.gen.openapi.generator

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfiguration
import org.platonos.rest.gen.openapi.OpenApiGeneratorConfigurationBuilder
import org.platonos.rest.gen.openapi.Options
import org.platonos.rest.gen.openapi.PlatformSupportJava
import java.io.File

internal class ApisGeneratorTest {

    private val apisGenerator = ApisGenerator()

    val dir = File("target/gen")

    @AfterEach
    fun tearDown() {
        deleteDir(dir)
    }

    @Test
    fun generatePaths() {
        val options = Options(
            fileName = "C:\\projects\\rest-dto\\demo\\openapi\\users.yml",
            modelPackageName = "org.some.models",
            apiPackageName = "org.some.api",
            generateApiDefintions = true
        )

        val openApi = Parser.parse(options.fileName)

        val config = OpenApiGeneratorConfigurationBuilder()
            .createConfig(options)

        val platformSupport = PlatformSupportJava()

       apisGenerator.generateApis(
            openApi,
            config,
            platformSupport,
            dir
        )
    }

    private fun deleteDir(dir: File) {
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }
}