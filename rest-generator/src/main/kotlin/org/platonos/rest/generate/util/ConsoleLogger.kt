package org.platonos.rest.generate.util

import java.io.PrintWriter
import java.io.StringWriter

class ConsoleLogger(private val name: String) : Logger() {

    override fun log(level: LogLevel, message: String, e: Throwable) {
        val extensionWriter = StringWriter()
        e.printStackTrace(PrintWriter(extensionWriter))
        log(level, "$message\nException:\n$extensionWriter")
    }

    override fun log(level: LogLevel, message: String) {
        if (level == LogLevel.SEVERE) {
            System.err.println(message)
        } else {
            println("$name: $message")
        }
    }
}