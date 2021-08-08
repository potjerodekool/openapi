package org.platonos.rest.build.maven

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.platonos.rest.gen.openapi.Build
import org.platonos.rest.gen.openapi.OpenApiGenerator
import org.platonos.rest.gen.openapi.Options
import org.platonos.rest.gen.util.Logger
import java.io.File

@Mojo(
    name = "generate-models",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
class PlatonosRestMojo : AbstractMojo() {

    @Parameter(defaultValue = "\${project.build.directory}/generated-sources", required = true)
    private lateinit var generatedSourceDirectory: File

    @Parameter(property = "model-package", required = false, defaultValue = "org.some.models")
    private lateinit var modelPackage: String

    @Parameter(property = "api-package", required = false, defaultValue = "org.some.api")
    private lateinit var apiPackage: String

    @Parameter(property = "file", required = true)
    private var openApiFile: String = ""

    @Parameter(property = "generateModels", required = false, defaultValue = "true")
    private var generateModels = true

    @Parameter(property = "generateApiDefintions", required = false, defaultValue = "false")
    private var generateApiDefintions = false

    @Parameter(property = "generateApiImplementations", required = false, defaultValue = "false")
    private var generateApiImplementations = false

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
            apiPackage = apiPackage,
            generateModels = generateModels,
            generateApiDefintions = generateApiDefintions,
            generateApiImplementations = generateApiImplementations
        )

        val build = Build(generatedSourceDirectory, generatedSourceDirectory)
        generator.generate(options, build)
    }
}