package klox.lox.src.main.kotlin

class Scanner(source: String) {
    private val tokens = mutableListOf<Token>()
    private val source = ""
    private var start = 0
    private var current = 0
    private var line = 1

    private val keywords = mutableMapOf<String, TokenType>()

    init {
        keywords.put("and", TokenType.AND)
        keywords.put("class", TokenType.CLASS)
        keywords.put("else", TokenType.ELSE)
        keywords.put("false", TokenType.FALSE)
        keywords.put("for", TokenType.FOR)
        keywords.put("fun", TokenType.FUN)
        keywords.put("if", TokenType.IF)
        keywords.put("nil", TokenType.NIL)
        keywords.put("or", TokenType.OR)
        keywords.put("print", TokenType.PRINT)
        keywords.put("return", TokenType.RETURN)
        keywords.put("super", TokenType.SUPER)
        keywords.put("this", TokenType.THIS)
        keywords.put("true", TokenType.TRUE)
        keywords.put("var", TokenType.VAR)
        keywords.put("while", TokenType.WHILE)
    }

    // add tokens until scanner runs out of characters,
    // appending an EOF token at the end
    fun scanTokens(): MutableList<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    fun isAtEnd(): Boolean {
        return current >= source.length
    }

    fun scanToken() {
        var c = advance()
        val Lox = Lox()

        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> {
                if (peek() == '*') {
                    while (peek() != '*' || peekNext() != '/') advance()
                    // two advances to eat star and slash
                    advance()
                    advance()
                } else if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(TokenType.SLASH)
                }
            }

            // skip whitespace
            ' ', '\r', '\t' -> { }

            '\n' -> line++

            '"' -> string()

            else -> if (isDigit(c)) {
                number()
            } else if (isAlpha(c)) {
                identifier()
            } else {
                Lox.error(line, "Unexpected character.")
            }
        }
    }

    fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val text = source.substring(start, current)
        var type = keywords.get(text)
        if (type == null) type = TokenType.IDENTIFIER
        addToken(type)
    }

    fun number() {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            advance()

            while (isDigit(peek())) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    fun string() {
        val Lox = Lox()
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        advance()

        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    // only consume current char if it's what we're looking for
    fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source.get(current) != expected) return false

        current++
        return true
    }

    // one character lookahead, not consuming the character
    fun peek(): Char? {
        if (isAtEnd()) return null
        return source.get(current)
    }

    // peek at character that's two characters ahead
    fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source.get(current + 1)
    }

    fun isAlpha(c: Char?): Boolean {
        if (c != null) {
            return (c >= 'a' && c <= 'z') ||
                    (c >= 'A' && c <= 'Z') ||
                    c == '_'
        } else {
            return false
        }
    }

    fun isAlphaNumeric(c: Char?): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    fun isDigit(c: Char?): Boolean {
        if (c != null) {
            return c >= '0' && c <= '9'
        } else {
            return false
        }
    }

    // consume next character in the source file
    fun advance(): Char {
        return source.get(current++)
    }

    // grab the text of current lexeme, and create new token for it
    fun addToken(type: TokenType) {
        addToken(type, null)
    }

    fun addToken(type: TokenType, literal: Any?) {
        val text: String = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }
}