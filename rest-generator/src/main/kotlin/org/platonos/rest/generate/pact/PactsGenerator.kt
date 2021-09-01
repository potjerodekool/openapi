package org.platonos.rest.generate.pact

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.reprezen.kaizen.oasparser.OpenApiParser
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Path
import org.platonos.rest.generate.openapi.createControllerKey
import java.io.File
import java.util.*

class PactsGenerator {

    private val parser = OpenApiParser()
    private val objectMapper = createObjectMapper()

    private val pactGenerators = mutableMapOf<String, PactGenerator>()

    private fun createObjectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())

        val fmt = StdDateFormat()
            .withTimeZone(TimeZone.getTimeZone("UTC"))
            .withColonInTimeZone(true)
        mapper.dateFormat = fmt

        return mapper
    }

    fun generate(file: File,
                 targetDir: File) {
        val openApi3 = parser.parse(file) as OpenApi3

        val schemaIdResolver = SchemaIdResolver()
        schemaIdResolver.visitOpenApi(openApi3)

        openApi3.paths.forEach { (requestPath, path) ->
            visitPath(requestPath, path, schemaIdResolver)
        }

        if (targetDir.exists().not()) {
            targetDir.mkdirs()
        }

        pactGenerators.values.forEach { generator ->
            val pact = generator.createPact()
            val pactJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pact)
            val pactFile = File(targetDir, generator.pactName + ".json")
            pactFile.writeText(pactJson)
        }
    }

    private fun visitPath(requestPath: String, path: Path, schemaIdResolver: SchemaIdResolver) {
        val controllerKey = createControllerKey(requestPath)
        val pactGenerator = pactGenerators.computeIfAbsent(controllerKey) {
            PactGenerator(controllerKey, schemaIdResolver)
        }

        pactGenerator.visitPath(requestPath, path)
    }

}