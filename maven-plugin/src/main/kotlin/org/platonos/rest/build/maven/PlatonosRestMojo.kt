package org.platonos.rest.build.maven

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.platonos.rest.gen.openapi.Build
import org.platonos.rest.gen.openapi.OpenApiGenerator
import org.platonos.rest.gen.openapi.Options
import org.platonos.rest.gen.pact.PactsGenerator
import org.platonos.rest.gen.util.Logger
import java.io.File

@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
class PlatonosRestMojo : AbstractMojo() {

    @Parameter(defaultValue = "\${project.build.directory}/generated-sources", required = true)
    private lateinit var generatedSourceDirectory: File

    @Parameter(property = "model-package", required = false)
    private var modelPackage: String? = null

    @Parameter(property = "api-package", required = false)
    private var apiPackageName: String? = null

    @Parameter(property = "openApiFile", required = true)
    private var openApiFile: String = ""

    @Parameter(property = "generateModels", required = false, defaultValue = "true")
    private var generateModels = true

    @Parameter(property = "generateApiDefintions", required = false, defaultValue = "false")
    private var generateApiDefintions = false

    @Parameter(property = "generateApiImplementations", required = false, defaultValue = "false")
    private var generateApiImplementations = false

    @Parameter(property = "dynamicModels", required = false)
    private var dynamicModels: String? = null

    @Parameter(property = "generatePacts", required = false, defaultValue = "false")
    private var generatePacts: Boolean = false

    override fun execute() {
        if (openApiFile.isEmpty()) {
            log.warn(""" 
                | No files specified to process.
                | Specify a file via configuration of the plugin.
                | Files must have a yml or yaml file extension.
                | 
                | <configuration>
                |   <openApiFile>openapi.yml</openApiFiles>
                | </configuration>
            """.trimMargin())
        }

        Logger.setLoggerFactory(MavenLoggerFactory(this))

        val generator = OpenApiGenerator()

        val options = Options(
            fileName = openApiFile,
            modelPackageName = modelPackage,
            apiPackageName = apiPackageName,
            generateModels = generateModels,
            generateApiDefintions = generateApiDefintions,
            generateApiImplementations = generateApiImplementations,
            dynamicModels = parseDynamicModels()
        )

        val build = Build(generatedSourceDirectory, generatedSourceDirectory)
        generator.generate(options, build)

        if (generatePacts) {
            val pactsGenerator = PactsGenerator()
            pactsGenerator.generate(File(openApiFile), File("target/test-classes/generated-pacts"))
        }

    }

    private fun parseDynamicModels(): List<String> {
        val dModels = dynamicModels

        if (dModels == null) {
            return emptyList()
        } else {
            return dModels.replace("\r", "")
                .split("\n")
                .toList()
        }
    }
}