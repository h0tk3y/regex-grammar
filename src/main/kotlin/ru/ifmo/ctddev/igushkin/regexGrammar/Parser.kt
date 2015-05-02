package ru.ifmo.ctddev.igushkin.regexGrammar

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

/**
 * Created by Sergey on 02.05.2015.
 */

public class Node(val name: String, val literal: Boolean = false, vararg knownChildren: Node) {
    val children: java.util.ArrayList<Node> = java.util.ArrayList();

    init {
        children.addAll(knownChildren)
    }

    override fun toString(): String = "$name" +
            if (children.size() > 0)
                "<" + children.fold("") { acc, x -> acc + x } + ">" else
                ""

    fun toExpression(): String = if (literal) "$name" else children.fold("") { acc, x -> acc + x.toExpression() }
}

public class ParserException(val pos: Int, message: String? = null, cause: Exception? = null)
: Exception("Could not parse at pos $pos"
        + if (message != null) ": $message"
        + if (cause != null) ": $cause")

public class Parser private (val input: java.io.InputStream) {

    val tokenizer = Tokenizer(input)

    init {
        tokenizer.nextToken()
    }

    companion object {
        public fun parse(s: String): Node = parse(java.io.ByteArrayInputStream(s.toByteArray("UTF-8")))
        public fun parse(input: java.io.InputStream): Node {
            val parser = Parser(input)
            val result = parser.S()
            if (parser.tokenizer.currentToken!!.type != TokenType.END) {
                throw ParserException(parser.tokenizer.currentPos, "Couldn't proceed parsing")
            }
            return result
        }
    }

    fun consume(t: TokenType): Boolean = when (tokenizer.currentToken!!.type) {
        t -> {
            tokenizer.nextToken()
            true
        }
        else -> false
    }

    fun S(): Node {
        val result = Node("S")
        result.children add
                when (tokenizer.currentToken!!.type) {
                    TokenType.CHARACTER, TokenType.LEFT_PARENTHESIS -> T()
                    else -> throw ParserException(tokenizer.currentPos)
                }

        if (consume(TokenType.OR)) {
            result.children.add(Node("|", true))
            result.children.add(S())
        }
        return result
    }

    private fun T(): Node {
        val result = Node("T")
        result.children add N()
        result.children add X()
        while (tokenizer.currentToken!!.type == TokenType.CHARACTER ||
                tokenizer.currentToken!!.type == TokenType.LEFT_PARENTHESIS) {
            result.children add T()
        }
        return result
    }

    private fun N(): Node {
        val result = Node("N")
        when (tokenizer.currentToken!!.type) {
            TokenType.CHARACTER -> {
                result.children add Node((tokenizer.currentToken as CharToken).character.toString(), true)
                consume(TokenType.CHARACTER)
            }
            TokenType.LEFT_PARENTHESIS -> {
                result.children add Node("(", true)
                consume(TokenType.LEFT_PARENTHESIS)
                result.children add S()
                if (!consume(TokenType.RIGHT_PARENTHESIS)) {
                    throw ParserException(tokenizer.currentPos, "Expected ')'")
                }
                result.children add Node(")", true)
            }
            else -> throw ParserException(tokenizer.currentPos, "Expected '(' or 'a'..'z'")
        }
        return result
    }

    private fun X(): Node? {
        val result = Node("X")
        if (consume(TokenType.ZERO_OR_MORE)) {
            result.children add Node("*", true)
        }
        return result
    }
}