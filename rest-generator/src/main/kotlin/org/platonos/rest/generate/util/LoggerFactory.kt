package org.platonos.rest.generate.util

interface LoggerFactory {

    fun getLogger(name: String): Logger
    
}