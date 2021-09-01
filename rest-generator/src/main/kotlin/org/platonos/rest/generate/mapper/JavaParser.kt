package org.platonos.rest.generate.mapper

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import java.io.File

class JavaParser(classLoader: ClassLoader) {

    init {
        val combinedTypeSolver = CombinedTypeSolver()
        combinedTypeSolver.add(ClassLoaderTypeSolver(classLoader))
        val symbolSolver = JavaSymbolSolver(combinedTypeSolver)
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver)
    }

    fun parse(file: File): CompilationUnit? {
        return StaticJavaParser.parse(file)
    }

}