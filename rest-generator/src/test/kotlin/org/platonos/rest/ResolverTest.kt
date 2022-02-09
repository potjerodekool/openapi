package org.platonos.rest

import com.github.javaparser.JavaParser
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.LambdaExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import org.junit.jupiter.api.Test
import java.io.File


class ResolverTest {

    @Test
    fun test() {

        val cu = JavaParser().parse(File("C:\\projects\\rest-dto\\rest-generator\\source\\org\\some\\model\\UserRequestDto.java"))
        println(cu)

        /*


        val combinedTypeSolver = CombinedTypeSolver()
        val reflectionTypeResolver = ReflectionTypeSolver(false)
        combinedTypeSolver.add(reflectionTypeResolver)

        // Configure JavaParser to use type resolution

        // Configure JavaParser to use type resolution
        val symbolSolver = JavaSymbolSolver(combinedTypeSolver)
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver)

        val cu = CompilationUnit()

        val clazz = ClassOrInterfaceDeclaration()
        clazz.setName("SomeClass")
        val method = clazz.addMethod("someMethod")

        val responseEntityType = combinedTypeSolver.solveType("org.springframework.http.ResponseEntity")
        val expr = responseEntityType.invokeStatic("notFound")


        val stm = NodeList<Statement>(ExpressionStmt(
            MethodCallExpr()
                .setScope(NameExpr("org.springframework.http.ResponseEntity"))
                .setName("ok")
                .addArgument(NameExpr("it"))
        ))

        val exp = MethodCallExpr()
            .setScope(NameExpr("result"))
            .setName("orElse")
            .addArgument(LambdaExpr()
                .addParameter(
                    Parameter()
                        .setType("java.lang.String")
                        .setName("it")
                ).setBody(BlockStmt(stm))
            )

        val statements = NodeList<Statement>(ExpressionStmt(exp))

        method.setBody(BlockStmt(statements))

        cu.setPackageDeclaration("org.some")
        cu.addType(clazz)

        println(cu)
*/
        /*


            //.asReferenceType() as JavaParserClassDeclaration

            //.asReferenceType() as JavaParserClassDeclaration



        //val method = resolvedType.solveMethod("notFound", mutableListOf(), true)
        val methods = resolvedType.declaredMethods

        val statements = NodeList<Statement>(ExpressionStmt(MethodCallExpr("")))

        val md = MethodDeclaration()
        md.setBody(BlockStmt(statements))

        println(resolvedType)
*/
    }
}