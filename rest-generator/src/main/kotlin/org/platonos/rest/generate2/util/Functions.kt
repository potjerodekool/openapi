package org.platonos.rest.generate2.util

object Functions {

    fun String.replaceFirstChar(mapper: (Char) -> Char): String {
        val firstChar: Char = mapper(this[0])
        return if (length == 1) firstChar.toString() else firstChar + substring(1, length)
    }

}