package org.platonos.rest.gen.openapi

class OpenApiGeneratorConfigurationBuilder {

    var modelPackageName: String = "org.some.models"

    var apiPackageName: String = "org.some.api"

    var generateModels: Boolean = true

    var generateApiDefintions: Boolean = true

    var generateApiImplementations: Boolean = true

    var generator: String = "spring"

    val dynamicModels = mutableListOf<String>()

    fun createConfig(options: Options): OpenApiGeneratorConfiguration {
        if (options.modelPackageName != null) {
            modelPackageName = options.modelPackageName
        }

        if (options.apiPackageName != null) {
            apiPackageName = options.apiPackageName
        }

        generateModels = options.generateModels

        generateApiDefintions = options.generateApiDefintions

        generateApiImplementations = options.generateApiImplementations

        if (options.generator != null) {
            generator = options.generator
        }

        dynamicModels.addAll(options.dynamicModels)

        return build()
    }

    fun build(): OpenApiGeneratorConfiguration {
        return OpenApiGeneratorConfiguration(this)
    }
}