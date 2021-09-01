package org.platonos.liquibase

import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.DatabaseChangeLog
import liquibase.parser.core.yaml.YamlChangeLogParser
import liquibase.resource.FileSystemResourceAccessor
import java.io.File

class LiquibaseGenerator {

    fun generate() {
        val yaml = loadChangeLog()
        val changeSets = yaml.changeSets

        val visitor = ChangeLogVisitor()
        visitor.visit(yaml)

        println(yaml)
    }

    private fun loadChangeLog(): DatabaseChangeLog {
        return try {
            val parser = YamlChangeLogParser()
            val baseDir = "C:\\projects\\rest-dto\\demo\\src\\main\\resources\\database\\changelog"
            val changeLogFile = "$baseDir\\changelog.yml"
            val resourceAccessor = FileSystemResourceAccessor(File(baseDir))
            parser.parse(changeLogFile, ChangeLogParameters(), resourceAccessor)
        } catch (e: Exception) {
            return DatabaseChangeLog()
        }
    }
}

fun main() {
    LiquibaseGenerator().generate()
}