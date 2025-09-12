import klox.lox.src.main.kotlin.Expr
import klox.lox.src.main.kotlin.Token

sealed class Stmt {
    interface Visitor<R> {
        fun visitExpressionStmt(stmt: Expression): R
        fun visitPrintStmt(stmt: Print): R
        fun visitVarStmt(stmt: Var): R
        fun visitBlockStmt(stmt: Block): R
        fun visitIfStmt(stmt: If): R
        fun visitWhileStmt(stmt: While): R
        fun visitFunctionStmt(stmt: Function): R
        fun visitReturnStmt(stmt: Return): R
        fun visitClassStmt(stmt: Class): R
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    data class Expression(
        val expression: Expr
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitExpressionStmt(this)
    }

    data class Print(
        val expression: Expr
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitPrintStmt(this)
    }

    data class Var(
        val name: Token,
        val initializer: Expr?
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitVarStmt(this)
    }

    data class Block(
        val statements: List<Stmt>
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitBlockStmt(this)
    }

    data class If(
        val condition: Expr,
        val thenBranch: Stmt,
        val elseBranch: Stmt?
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitIfStmt(this)
    }

    data class While(
        val condition: Expr,
        val body: Stmt
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitWhileStmt(this)
    }

    data class Function(
        val name: Token,
        val params: List<Token>,
        val body: List<Stmt>
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitFunctionStmt(this)
    }

    data class Return(
        val keyword: Token,
        val value: Expr?
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitReturnStmt(this)
    }

    data class Class(
        val name: Token,
        val superclass: Expr.Variable?,
        val methods: List<Function>
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitClassStmt(this)
    }
}