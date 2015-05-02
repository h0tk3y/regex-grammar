package ru.ifmo.ctddev.igushkin.regexGrammar

/**
 * Created by Sergey on 02.05.2015.
 */

import org.junit.Test as test
import org.junit.Assert.*
import org.junit.Test

public class ParserTest {

    fun parseAndCheck(expr: String, tree: String) {
        val result = Parser.parse(expr)
        assertEquals(expr, result.toExpression())
        assertEquals(tree, result.toString())
    }

    Test fun simple() {
        parseAndCheck("p", "S<T<N<p>X>>")
        parseAndCheck("a*", "S<T<N<a>X<*>>>")
        parseAndCheck("a|b", "S<T<N<a>X>|S<T<N<b>X>>>")
    }

    Test fun hard() {
        parseAndCheck("((abc*b|a)*ab(aa|b*)b)*",
                "S<T<N<(S<T<N<(S<T<N<a>XT<N<b>XT<N<c>X<*>T<N<b>X>>>>|S<T<N<a>X>>>)>X<*>T<N<a>XT<N<b>XT<N<(S<T<N<a>XT<N<a>X>>|S<T<N<b>X<*>>>>)>XT<N<b>X>>>>>>)>X<*>>>")
        parseAndCheck("(a*|a*)(a*|a*)",
                "S<T<N<(S<T<N<a>X<*>>|S<T<N<a>X<*>>>>)>XT<N<(S<T<N<a>X<*>>|S<T<N<a>X<*>>>>)>X>>>")
        parseAndCheck("(a|b|c|(a|b|c)|d|d*|(d*)*)*",
                "S<T<N<a>X>|S<T<N<b>X>|S<T<N<c>X>|S<T<N<(S<T<N<a>X>|S<T<N<b>X>|S<T<N<c>X>>>>)>X>|S<T<N<d>X>|S<T<N<d>X<*>>|S<T<N<(S<T<N<d>X<*>>>)>X<*>>>>>>>>>")
    }

    Test fun concat() {
        parseAndCheck("(a)(b)",
                "S<T<N<(S<T<N<a>X>>)>XT<N<(S<T<N<b>X>>)>X>>>")
        parseAndCheck("abcdefg",
                "S<T<N<a>XT<N<b>XT<N<c>XT<N<d>XT<N<e>XT<N<f>XT<N<g>X>>>>>>>>")
    }

    Test fun or() {
        parseAndCheck("a*|b", "S<T<N<a>X<*>>|S<T<N<b>X>>>")
        parseAndCheck("abc|def|(g*|h*)", "S<T<N<a>XT<N<b>XT<N<c>X>>>|S<T<N<d>XT<N<e>XT<N<f>X>>>|S<T<N<(S<T<N<g>X<*>>|S<T<N<h>X<*>>>>)>X>>>>")
    }

    Test fun zeroOrMore() {
        parseAndCheck("((a*)*)*",
                "S<T<N<(S<T<N<(S<T<N<a>X<*>>>)>X<*>>>)>X<*>>>")
        parseAndCheck("(a*bc|(de)*f)*",
                "S<T<N<(S<T<N<a>X<*>T<N<b>XT<N<c>X>>>|S<T<N<(S<T<N<d>XT<N<e>X>>>)>X<*>T<N<f>X>>>>)>X<*>>>")
    }

    Test(expected = javaClass<ParserException>()) fun testFail1() {
        Parser.parse("abc(|")
    }

    Test(expected = javaClass<ParserException>()) fun testFail2() {
        Parser.parse("asdf||")
    }

    Test(expected = javaClass<ParserException>()) fun testFail3() {
        Parser.parse("|x")
    }

    Test(expected = javaClass<ParserException>()) fun testFail4() {
        Parser.parse("x**")
    }

    Test(expected = javaClass<ParserException>()) fun testFail5() {
        Parser.parse("abc|(abc*|(x)")
    }
}