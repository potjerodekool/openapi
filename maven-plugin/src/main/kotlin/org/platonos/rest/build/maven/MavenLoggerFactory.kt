package org.platonos.rest.build.maven

import org.apache.maven.plugin.Mojo
import org.platonos.rest.gen.util.LogLevel
import org.platonos.rest.gen.util.Logger
import org.platonos.rest.gen.util.LoggerFactory

class MavenLoggerFactory(private val mojo: Mojo) : LoggerFactory {

    override fun getLogger(name: String): Logger {
        return MavenLogger(mojo, name)
    }
}

class MavenLogger(private val mojo: Mojo, private val name: String): Logger() {

    override fun log(level: LogLevel, message: String, e: Throwable) {
        val logMessage = "$name: $message"

        when (level) {
            LogLevel.SEVERE -> mojo.log.error(logMessage, e)
            LogLevel.WARNING -> mojo.log.warn(logMessage, e)
            else -> mojo.log.info(logMessage)
        }
    }

    override fun log(level: LogLevel, message: String) {
        val logMessage = "$name: $message"

        when (level) {
            LogLevel.SEVERE -> mojo.log.error(logMessage)
            LogLevel.WARNING -> mojo.log.warn(logMessage)
            else -> mojo.log.info(logMessage)
        }
    }


}