package klox.lox.src.main.kotlin

class Interpreter: Expr.Visitor<Any> {
    fun interpret(expression: Expr?) {
        try {
            val value = evaluate(expression)
            println(stringify(value))
        } catch (error: RuntimeError) {
            runtimeError(error)
        }
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any {
        return expr.value as Object
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    fun evaluate(expr: Expr?): Any? {
        return expr?.accept(this)
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                return -(right as Double)
            }
            // case TokenType.BANG
            else -> return !(isTruthy(right))
        }

        return null
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.right)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                return ((left as Double) - (right as Double))
            }
            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                return ((left as Double) / (right as Double))
            }
            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                return ((left as Double) * (right as Double))
            }
            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    return (left + right)
                }

                if (left is String && right is String) {
                    return (left + right)
                }

                throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
            }
            TokenType.GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                return ((left as Double) > (right as Double))
            }
            TokenType.GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return ((left as Double) >= (right as Double))
            }
            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return ((left as Double) < (right as Double))
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return ((left as Double) <= (right as Double))
            }
            TokenType.BANG_EQUAL -> return !(isEqual(left, right))
            TokenType.EQUAL_EQUAL -> return (isEqual(left, right))
            else -> return null
        }

        return null
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false

        return a.equals(b)
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"

        if (obj is Double) {
            var text = obj.toString()

            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }

            return text
        }

        return obj.toString()
    }
}