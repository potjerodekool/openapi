package org.platonos.rest.gen.openapi.generator.api

import org.platonos.rest.gen.openapi.generator.UnsupportedGeneratorException
import org.platonos.rest.gen.openapi.generator.api.spring.ApiGeneratorSpring

object ApiGeneratorFactory {

    fun createGenerator(generatorName: String): ApiGenerator {
        return when(generatorName) {
            "spring" -> ApiGeneratorSpring()
            else -> throw UnsupportedGeneratorException(generatorName)
        }
    }
}