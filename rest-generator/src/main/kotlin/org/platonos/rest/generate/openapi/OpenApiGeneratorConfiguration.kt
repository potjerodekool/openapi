package org.platonos.rest.generate.openapi

class OpenApiGeneratorConfiguration(openApiGeneratorConfigurationBuilder: OpenApiGeneratorConfigurationBuilder) {

    val modelPackageName = openApiGeneratorConfigurationBuilder.modelPackageName

    val apiPackageName = openApiGeneratorConfigurationBuilder.apiPackageName

    val generateModels = openApiGeneratorConfigurationBuilder.generateModels

    val generateApiDefinitions = openApiGeneratorConfigurationBuilder.generateApiDefintions

    val generator = openApiGeneratorConfigurationBuilder.generator

    val generateApiImplementations = openApiGeneratorConfigurationBuilder.generateApiImplementations

    val dynamicModels = openApiGeneratorConfigurationBuilder.dynamicModels

    val modelNamingStrategy: ModelNamingStrategy = DefaultModelNamingStrategy()
}