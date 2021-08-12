package org.platonos.rest.gen.openapi

class OpenApiGeneratorConfiguration(openApiGeneratorConfigurationBuilder: OpenApiGeneratorConfigurationBuilder) {

    val modelPackageName = openApiGeneratorConfigurationBuilder.modelPackageName

    val apiPackageName = openApiGeneratorConfigurationBuilder.apiPackageName

    val generateModels = openApiGeneratorConfigurationBuilder.generateModels

    val generateApiDefintions = openApiGeneratorConfigurationBuilder.generateApiDefintions

    val generator = openApiGeneratorConfigurationBuilder.generator

    val generateApiImplementations = openApiGeneratorConfigurationBuilder.generateApiImplementations

    val dynamicModels = openApiGeneratorConfigurationBuilder.dynamicModels

    val modelNamingStrategy: ModelNamingStrategy = DefaultModelNamingStrategy()
}