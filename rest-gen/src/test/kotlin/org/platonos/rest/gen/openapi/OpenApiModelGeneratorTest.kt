package org.platonos.rest.gen.openapi

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import java.io.File

internal class OpenApiModelGeneratorTest {

    val generator = OpenApiGenerator()
    val sourceDir = File("target/gen")

    @AfterEach
    fun tearDown() {
        deleteDir(sourceDir)
    }

    @Test
    fun generate() {
        deleteDir(sourceDir)

        sourceDir.mkdirs()

        val apiFileName = "C:\\projects\\rest-dto\\demo\\openapi\\users.yml"
        val build = Build(sourceDir, sourceDir)
        val options = Options(
            fileName = apiFileName,
            modelPackageName = "org.some.models",
            dynamicModels = listOf("User")
        )

        generator.generate(options, build)
    }

    private fun deleteDir(dir: File) {
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }
}