package org.platonos.rest.gen.util

class ConsoleLoggerFactory: LoggerFactory {

    override fun getLogger(name: String): Logger {
        return ConsoleLogger(name)
    }
}