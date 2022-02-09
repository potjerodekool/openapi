package org.platonos.rest.model

import org.junit.jupiter.api.Test
import org.platonos.rest.generate.Build
import org.platonos.rest.generate.ProjectInfo
import org.platonos.rest.openapi.OpenApiGeneratorConfigurationBuilder
import org.platonos.rest.openapi.Options
import org.platonos.rest.generate2.Generator2
import java.io.File

internal class ModelBuilderTest {

    val sourceDir = File("source")

    @Test
    fun buildModels() {
        if (sourceDir.exists()) {
            sourceDir.deleteRecursively()
        }

        val fileName = "C:\\projects\\rest-dto\\demo\\openapi\\spec.yml"
        val options = Options(
            fileName = fileName,
            modelPackageName = "org.some.model",
            apiPackageName =  "org.some.api",
            generateApiDefintions = true,
            generateApiImplementations = true,
            features = emptyMap()
        )

        val config = OpenApiGeneratorConfigurationBuilder()
            .createConfig(options)

        val projectInfo = ProjectInfo(
            sourceRoots = listOf(sourceDir.absolutePath),
            classPath = listOf(),
            build = Build(
                sourceDir
            )
        )

        val gen = Generator2(options, projectInfo)
        gen.execute()
    }
}