package org.platonos.rest.gen.openapi.generator.api

import org.platonos.rest.gen.openapi.generator.UnsupportedGeneratorException
import org.platonos.rest.gen.openapi.generator.api.spring.ApiDefinitionGeneratorSpring
import org.platonos.rest.gen.openapi.generator.api.spring.ApiImplementationGeneratorSpring
import org.platonos.rest.gen.openapi.generator.api.spring.UtilsGeneratorSpring

object ApiGeneratorFactory {

    fun createApiDefinitionGenerator(generatorName: String): ApiDefinitionGenerator {
        return when(generatorName) {
            "spring" -> ApiDefinitionGeneratorSpring()
            else -> throw UnsupportedGeneratorException(generatorName)
        }
    }

    fun createApiImplementationGenerator(generatorName: String): ApiImplementationGenerator {
        return when(generatorName) {
            "spring" -> ApiImplementationGeneratorSpring()
            else -> throw UnsupportedGeneratorException(generatorName)
        }
    }

    fun createUtilsGenerator(generatorName: String): UtilsGenerator {
        return when(generatorName) {
            "spring" -> UtilsGeneratorSpring()
            else -> throw UnsupportedGeneratorException(generatorName)
        }
    }
}