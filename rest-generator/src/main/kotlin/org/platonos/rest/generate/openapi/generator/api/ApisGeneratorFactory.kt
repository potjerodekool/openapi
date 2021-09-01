package org.platonos.rest.generate.openapi.generator.api

import org.platonos.rest.generate.openapi.generator.api.spring.ApisGeneratorSpring

object ApisGeneratorFactory {

    fun createApisGenerator(generatorName: String): ApisGenerator {
        return when(generatorName) {
            "spring" -> ApisGeneratorSpring()
            else -> throw RuntimeException("Unsupported generator $generatorName")
        }
    }
}