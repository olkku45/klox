package klox.lox.src.main.kotlin

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

var hadError = false

fun main(args: Array<String>) {
    if (args.size > 1) {
        System.exit(64)
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}

fun runFile(filePath: String) {
    val bytes = Files.readAllBytes(Paths.get(filePath))
    run(String(bytes, Charset.defaultCharset()))

    if (hadError) System.exit(65)
}

fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")
        val line = reader.readLine()

        if (line == null) break
        run(line)
        hadError = false
    }
}

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    val expression: Expr? = parser.parse()

    if (hadError) return

    println(expression)
}

fun error(line: Int, message: String) {
    report(line, "", message)
}

fun report(line: Int, where: String, message: String) {
    System.err.println("[line " + line + "] Error" + where + ": " + message)
    hadError = true
}

fun tokenError(token: Token, message: String) {
    if (token.type == TokenType.EOF) {
        report(token.line, " at end", message)
    } else {
        report(token.line, " at '" + token.lexeme + "'", message)
    }
}