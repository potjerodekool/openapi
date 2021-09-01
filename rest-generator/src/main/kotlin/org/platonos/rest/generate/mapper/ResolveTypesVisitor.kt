package org.platonos.rest.generate.mapper

import com.github.javaparser.ast.*
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.comments.BlockComment
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.comments.LineComment
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.modules.*
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.type.*
import com.github.javaparser.ast.visitor.GenericVisitor

class ResolveTypesVisitor : GenericVisitor<Any, Any> {

    override fun visit(n: ClassOrInterfaceDeclaration, arg: Any?): Any {
        n.methods.forEach { methodDeclaration ->
            methodDeclaration.accept(this, arg)
        }

        return n
    }

    override fun visit(n: ClassOrInterfaceType, arg: Any?): Any {
        return n.resolve()
    }

    override fun visit(n: CompilationUnit, arg: Any?): Any {
        n.types.forEach { typeDeclaration ->
            typeDeclaration.accept(this, arg)
        }

        return n
    }

    override fun visit(n: MethodDeclaration, arg: Any?): Any {
        n.type = n.type.accept(this, arg) as Type
        n.parameters = n.parameters.map { parameter -> parameter.accept(this, arg) } as NodeList<Parameter>
        return n
    }

    override fun visit(n: PackageDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: TypeParameter?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: LineComment?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: BlockComment?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: RecordDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: CompactConstructorDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: EnumDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: EnumConstantDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: AnnotationDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: AnnotationMemberDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: FieldDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: VariableDeclarator?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ConstructorDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: Parameter?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: InitializerDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: JavadocComment?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: PrimitiveType?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ArrayType?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ArrayCreationLevel?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: IntersectionType?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: UnionType?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: VoidType?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: WildcardType?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: UnknownType?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ArrayAccessExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ArrayCreationExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ArrayInitializerExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: AssignExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: BinaryExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: CastExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ClassExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ConditionalExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: EnclosedExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: FieldAccessExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: InstanceOfExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: StringLiteralExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: IntegerLiteralExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: LongLiteralExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: CharLiteralExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: DoubleLiteralExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: BooleanLiteralExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: NullLiteralExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: MethodCallExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: NameExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ObjectCreationExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ThisExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: SuperExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: UnaryExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: VariableDeclarationExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: MarkerAnnotationExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: SingleMemberAnnotationExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: NormalAnnotationExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: MemberValuePair?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ExplicitConstructorInvocationStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: LocalClassDeclarationStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: LocalRecordDeclarationStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: AssertStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: BlockStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: LabeledStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: EmptyStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ExpressionStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: SwitchStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: SwitchEntry?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: BreakStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ReturnStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: IfStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: WhileStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ContinueStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: DoStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ForEachStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ForStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ThrowStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: SynchronizedStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: TryStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: CatchClause?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: LambdaExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: MethodReferenceExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: TypeExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: NodeList<*>?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: Name?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: SimpleName?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ImportDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ModuleDeclaration?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ModuleRequiresDirective?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ModuleExportsDirective?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ModuleProvidesDirective?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ModuleUsesDirective?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ModuleOpensDirective?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: UnparsableStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: ReceiverParameter?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: VarType?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: Modifier?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: SwitchExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: YieldStmt?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: TextBlockLiteralExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }

    override fun visit(n: PatternExpr?, arg: Any?): Any {
        TODO("Not yet implemented")
    }
}