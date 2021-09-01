package org.platonos.rest.generate.openapi.generator

class UnsupportedGeneratorException(generatorName: String): RuntimeException("Generator $generatorName is not supported") {
}