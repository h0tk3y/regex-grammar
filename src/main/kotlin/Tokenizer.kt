import java.io.IOException
import java.io.InputStream
import kotlin.*;
import kotlin.properties.Delegates

/**
 * Created by Sergey on 01.05.2015.
 */

public enum class TokenType {
    LEFT_PARENTHESIS;
    RIGHT_PARENTHESIS;
    OR;
    ZERO_OR_MORE;
    CHARACTER;
    END

    companion object {
        fun parse(c: Char) = when (c) {
            '(' -> LEFT_PARENTHESIS
            ')' -> RIGHT_PARENTHESIS
            '|' -> OR
            '*' -> ZERO_OR_MORE
            0.toChar() -> END
            in 'a'..'z' -> CHARACTER
            else -> null
        }
    }
}

public open class Token(val type: TokenType)
public class CharToken(val character: Char) : Token(TokenType.CHARACTER)

public class TokenizerException(val pos: Int, cause: Throwable? = null)
: Exception("Couldn't make token at pos $pos", cause)

public class Tokenizer(val input: InputStream) {
    private var currentChar: Char = 0.toChar()
    private var currentPos: Int = 0
    public var currentToken: Token? = null; private set

    public var endOfStream: Boolean = false; private set

    private fun blank(c: Char) = when (c) {
        ' ', '\r', '\n', '\t' -> true
        else -> false
    }

    private fun nextChar(): Char {
        try {
            val c = input.read()
            if (!endOfStream) {
                ++currentPos
                currentChar = (if (c == -1) {
                    endOfStream = true; 0
                } else c).toChar()
            }
        } catch (e: IOException) {
            throw TokenizerException(currentPos, e)
        }
        return currentChar
    }

    public fun nextToken(): Token {
        do {
            nextChar()
        } while (blank(currentChar) && !endOfStream)

        val type = TokenType.parse(currentChar)

        currentToken = when (type) {
            null -> throw TokenizerException(currentPos)
            TokenType.CHARACTER -> CharToken(currentChar)
            else -> Token(type)
        }
        return currentToken!!
    }
}