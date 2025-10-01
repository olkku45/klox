package klox.lox.src.main.kotlin

class RuntimeError(val token: Token, message: String): RuntimeException("$token: $message")