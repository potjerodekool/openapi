package org.platonos.rest.generate.openapi

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.platonos.rest.generate.Build
import org.platonos.rest.generate.Generator
import org.platonos.rest.generate.ProjectInfo

import java.io.File

internal class OpenApiModelGeneratorTest {

    val generator = Generator()
    val sourceDir = File("target/gen")

    @AfterEach
    fun tearDown() {
        deleteDir(sourceDir)
    }

    @Test
    fun generate() {
        deleteDir(sourceDir)

        sourceDir.mkdirs()

        val apiFileName = "C:\\projects\\rest-dto\\demo\\openapi\\spec.yml"
        val build = Build(sourceDir)
        val options = Options(
            fileName = apiFileName,
            modelPackageName = "org.some.models",
            dynamicModels = listOf("User")
        )

        val projectInfo = ProjectInfo(
            listOf(),
            listOf(),
            build
        )

        generator.generate(options, projectInfo)
    }

    private fun deleteDir(dir: File) {
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }
}