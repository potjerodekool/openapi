package org.platonos.rest.gen.util

interface LoggerFactory {

    fun getLogger(name: String): Logger
    
}