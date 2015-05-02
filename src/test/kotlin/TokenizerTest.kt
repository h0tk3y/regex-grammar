/**
 * Created by Sergey on 01.05.2015.
 */

import org.junit.Assert.*
import java.io.ByteArrayInputStream
import java.util.ArrayList
import org.junit.Test as test

fun strToStream(s: String): ByteArrayInputStream {
    return ByteArrayInputStream(s.toByteArray("UTF-8"))
}

public class TokenizerTest {

    fun getTokens(tokenizer: Tokenizer): ArrayList<Token> {
        val result = ArrayList<Token>()
        do {
            val t = tokenizer.nextToken();
            result add t
        } while (t.type != TokenType.END)
        return result
    }

    fun strToTokens(s: String) = getTokens(Tokenizer(strToStream(s)))

    val LBR = TokenType.LEFT_PARENTHESIS
    val RBR = TokenType.RIGHT_PARENTHESIS
    val OR = TokenType.OR
    val SOME = TokenType.ZERO_OR_MORE
    val END = TokenType.END
    val CHAR = TokenType.CHARACTER

    test fun testEmpty() {
        val tokens = strToTokens("")
        assertEquals(1, tokens.size())
        assertEquals(tokens[0].type, TokenType.END)
    }

    test fun testOneToken() {
        val tokens1 = strToTokens("a")
        assertEquals(listOf(CHAR, END), tokens1.map { it.type })
        val tokens2 = strToTokens("|")
        assertEquals(listOf(OR, END), tokens2.map { it.type })
    }

    test fun testSimple() {
        val tokens = strToTokens("(a)*|b")
        assertEquals(listOf(LBR, CHAR, RBR, SOME, OR, CHAR, END), tokens.map { it.type })
    }

    test fun testHard() {
        val tokens = strToTokens("((abc*b|a)*ab(aa|b*)b)*")
        assertEquals(listOf(LBR, LBR, CHAR, CHAR, CHAR, SOME, CHAR, OR, CHAR, RBR,
                SOME, CHAR, CHAR, LBR, CHAR, CHAR, OR, CHAR, SOME, RBR, CHAR, RBR,
                SOME, END),
                tokens.map { it.type })
    }

    test fun testSpaces() {
        val tokens1 = strToTokens("((abc*b|a)*ab(aa|b*)b)*")
        val tokens2 = strToTokens("( (a   b\tc*b\r|a) \n*ab  (a a|b   \t\t*)\r\nb ) *  ")
        assertEquals(tokens1.map { it.type }, tokens2.map { it.type })
    }

    test(expected = javaClass<TokenizerException>())
    fun testUnknownChars() {
        strToTokens("abcd!@#$")
    }


}