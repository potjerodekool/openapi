package org.platonos.rest.gen.openapi.generator

class UnsupportedGeneratorException(generatorName: String): RuntimeException("Generator $generatorName is not supported") {
}