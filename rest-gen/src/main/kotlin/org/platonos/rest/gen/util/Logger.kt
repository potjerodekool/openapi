package org.platonos.rest.gen.util

import java.io.PrintWriter
import java.io.StringWriter

abstract class Logger {

    companion object {

        private var factory: LoggerFactory = ConsoleLoggerFactory()

        fun setLoggerFactory(loggerFactory: LoggerFactory) {
            factory = loggerFactory
        }

        fun getLogger(clazz: Class<*>): Logger {
            return getLogger(clazz.name)
        }

        fun getLogger(name: String): Logger {
            return factory.getLogger(name)
        }
    }

    abstract fun log(level: LogLevel, message: String, e: Throwable)

    abstract fun log(level: LogLevel, message: String)

}