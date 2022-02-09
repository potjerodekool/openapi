package org.platonos.rest.generate2.util

class ConsoleLoggerFactory: LoggerFactory {

    override fun getLogger(name: String): Logger {
        return ConsoleLogger(name)
    }
}