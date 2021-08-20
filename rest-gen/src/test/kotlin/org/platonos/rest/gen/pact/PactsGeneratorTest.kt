package org.platonos.rest.gen.pact

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

@ExtendWith(MockKExtension::class)
internal class PactsGeneratorTest {

    @InjectMockKs
    private lateinit var pactsGenerator: PactsGenerator

    private val objectMapper = ObjectMapper()

    private fun loadJsonResource(resourceName: String): String {
        val text = javaClass.classLoader.getResource(resourceName).readText()
        val tree = objectMapper.readTree(text)
        return objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(tree)
    }

    private fun loadJsonFile(file: File): String {
        val tree = objectMapper.readTree(file)
        return objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(tree)
    }

    @Test
    fun generate() {
        val targetDir = File("target/generated/pacts")
        if (targetDir.exists().not()) {
            targetDir.mkdirs()
        }

        val file = File("C:\\projects\\rest-dto\\demo\\openapi\\users.yml")

        pactsGenerator.generate(file, targetDir)

        val expected = loadJsonResource("pacts/users.json")
        val actual = loadJsonFile(File(targetDir, "users.json"))
        assertEquals(expected, actual)
    }
}