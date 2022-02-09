package org.platonos.rest.generate2.util

interface LoggerFactory {

    fun getLogger(name: String): Logger
    
}