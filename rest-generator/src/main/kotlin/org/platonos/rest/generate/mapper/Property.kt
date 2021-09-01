package org.platonos.rest.generate.mapper

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.resolution.types.ResolvedType

class Property(val propertyName: String,
               val propertyType: ResolvedType,
               val getter: MethodDeclaration?,
               val setter: MethodDeclaration?)