package org.platonos.rest.generate2

import org.platonos.rest.backend.Filer
import org.platonos.rest.generate.ProjectInfo
import org.platonos.rest.openapi.*
import org.platonos.rest.openapi.api.Api
import org.platonos.rest.generate2.api.ApiDefinitionsGenerator
import org.platonos.rest.generate2.model.ModelsGenerator
import java.io.File

class Generator2(private val options: Options, projectInfo: ProjectInfo) {

    val config = OpenApiGeneratorConfigurationBuilder()
        .createConfig(options)

    private val platformSupport = PlatformSupportJava(
        config.modelNamingStrategy,
        config.modelPackageName
    )

    val types = TypesJava()

    val typeConverter = JavaTypeConverter(
        types,
        config.modelNamingStrategy,
        config.modelPackageName
    )

    val filer = Filer(projectInfo.build.sourceDir, platformSupport)

    fun execute() {
        options.features.forEach { (key, value) ->
            System.setProperty(key, value.toString())
        }

        val openApi = OpenApiMerger().merge(File(options.fileName))

        val apiBuilder = ApiBuilder(
            config,
            typeConverter,
            config.modelNamingStrategy
        )

        val api = apiBuilder.buildModels(openApi)
        generateModels(api)
        generateApi(api)
    }

    private fun generateModels(api: Api) {
        val generator = ModelsGenerator(config, filer)
        generator.generate(api)
    }

    private fun generateApi(api: Api) {
        val definitionGenerator = ApiDefinitionsGenerator(config, types, filer)
        definitionGenerator.generate(api)
    }

}