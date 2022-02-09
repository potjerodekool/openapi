package org.platonos.rest.openapi

import com.reprezen.kaizen.oasparser.OpenApiParser
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import java.io.File

class OpenApiMerger {

    private val parser = OpenApiParser()

    fun merge(file: File): OpenApi3 {
        val rootFile = parser.parse(file) as OpenApi3

        val dir = file.parentFile

        val pathsDir = File(dir, "paths")

        if (pathsDir.exists()) {
            importPaths(pathsDir, rootFile)
        }

        return rootFile
    }

    private fun importPaths(dir: File, rootFile: OpenApi3) {
        val files = dir.listFiles()

        if (files != null) {
            files.forEach { file ->
                if (file.isDirectory) {
                    importPaths(file, rootFile)
                } else if (file.extension == "yml" || file.extension == "yaml") {
                    importPathsFromFile(file, rootFile)
                }
            }
        }
    }

    private fun importPathsFromFile(file: File, rootFile: OpenApi3) {
        val openApi = parser.parse(file) as OpenApi3
        rootFile.paths.putAll(openApi.paths)
    }
}