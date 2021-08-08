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

        val apiFileName = "C:\\projects\\rest-dto\\demo\\rest-dto\\users.yml"
        val build = Build(sourceDir, sourceDir)
        val options = Options(
            apiFileName,
            "org.some.models",
            ""
        )

        generator.generate(options, build)
        //generator.generate(apiFileName, "org.platonos.rest.test.models", build)
    }

    private fun deleteDir(dir: File) {
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }
}