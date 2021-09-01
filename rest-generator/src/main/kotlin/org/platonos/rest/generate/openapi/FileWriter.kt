package org.platonos.rest.generate.openapi

import java.io.File

object FileWriter {

    fun write(targetDir: File,
              packageName: String,
              simpleName: String,
              sourceFileExtension: String,
              code: String): File {
        val packageDir = File(targetDir, packageName.replace('.', File.separatorChar))

        if (packageDir.exists().not()) {
            packageDir.mkdirs()
        }

        val outputFile = File(packageDir, "$simpleName.$sourceFileExtension")
        outputFile.writeText(code)
        return outputFile
    }
}