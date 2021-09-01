package org.platonos.rest.generate.util

class ConsoleLoggerFactory: LoggerFactory {

    override fun getLogger(name: String): Logger {
        return ConsoleLogger(name)
    }
}