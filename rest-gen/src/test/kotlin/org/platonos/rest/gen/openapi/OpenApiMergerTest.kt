package org.platonos.rest.gen.openapi

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File

internal class OpenApiMergerTest {

    @Test
    fun merge() {
        val merger = OpenApiMerger()
        val merged = merger.merge(File("C:\\projects\\rest-dto\\demo\\openapi\\users.yml"))
        println(merged)
    }
}