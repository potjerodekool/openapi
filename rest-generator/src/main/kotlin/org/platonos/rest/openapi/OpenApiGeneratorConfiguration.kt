package org.platonos.rest.openapi

class OpenApiGeneratorConfiguration(openApiGeneratorConfigurationBuilder: OpenApiGeneratorConfigurationBuilder) {

    val modelPackageName: String = openApiGeneratorConfigurationBuilder.modelPackageName

    val apiPackageName: String = openApiGeneratorConfigurationBuilder.apiPackageName

    val generateModels: Boolean = openApiGeneratorConfigurationBuilder.generateModels

    val generateApiDefinitions: Boolean = openApiGeneratorConfigurationBuilder.generateApiDefintions

    val generator: String = openApiGeneratorConfigurationBuilder.generator

    val generateApiImplementations: Boolean = openApiGeneratorConfigurationBuilder.generateApiImplementations

    val dynamicModels: List<String> = openApiGeneratorConfigurationBuilder.dynamicModels

    val modelNamingStrategy: ModelNamingStrategy = DefaultModelNamingStrategy()
}