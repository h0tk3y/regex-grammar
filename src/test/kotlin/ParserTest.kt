/**
 * Created by Sergey on 02.05.2015.
 */

import org.junit.Test as test
import org.junit.Assert.*

public class ParserTest {
    fun testTree(expr: String, tree: String) {
        val result = Parser.parse(expr)
        assertEquals(expr, result.toExpression())
        assertEquals(tree, result.toString())
    }

    test fun simple() {
        testTree("p", "S<T<N<p>X>>")
        testTree("a*", "S<T<N<a>X<*>>>")
        testTree("a|b", "S<T<N<a>X>|S<T<N<b>X>>>")
    }

    test fun hard() {
        testTree("((abc*b|a)*ab(aa|b*)b)*",
                "S<T<N<(S<T<N<(S<T<N<a>XT<N<b>XT<N<c>X<*>T<N<b>X>>>>|S<T<N<a>X>>>)>X<*>T<N<a>XT<N<b>XT<N<(S<T<N<a>XT<N<a>X>>|S<T<N<b>X<*>>>>)>XT<N<b>X>>>>>>)>X<*>>>")
        testTree("(a*|a*)(a*|a*)",
                "S<T<N<(S<T<N<a>X<*>>|S<T<N<a>X<*>>>>)>XT<N<(S<T<N<a>X<*>>|S<T<N<a>X<*>>>>)>X>>>")
        testTree("a|b|c|(a|b|c)|d|d*|(d*)*",
                "S<T<N<a>X>|S<T<N<b>X>|S<T<N<c>X>|S<T<N<(S<T<N<a>X>|S<T<N<b>X>|S<T<N<c>X>>>>)>X>|S<T<N<d>X>|S<T<N<d>X<*>>|S<T<N<(S<T<N<d>X<*>>>)>X<*>>>>>>>>>")
    }

    test fun concat() {
        testTree("(a)(b)",
                "S<T<N<(S<T<N<a>X>>)>XT<N<(S<T<N<b>X>>)>X>>>")
        testTree("abcdefg",
                "S<T<N<a>XT<N<b>XT<N<c>XT<N<d>XT<N<e>XT<N<f>XT<N<g>X>>>>>>>>")
    }

    test fun or() {
        testTree("a*|b", "S<T<N<a>X<*>>|S<T<N<b>X>>>")
        testTree("abc|def|(g*|h*)", "S<T<N<a>XT<N<b>XT<N<c>X>>>|S<T<N<d>XT<N<e>XT<N<f>X>>>|S<T<N<(S<T<N<g>X<*>>|S<T<N<h>X<*>>>>)>X>>>>")
    }

    test fun zeroOrMore() {
        testTree("((a*)*)*", "S<T<N<(S<T<N<(S<T<N<a>X<*>>>)>X<*>>>)>X<*>>>")
        testTree("(a*bc|(de)*f)*", "S<T<N<(S<T<N<a>X<*>T<N<b>XT<N<c>X>>>|S<T<N<(S<T<N<d>XT<N<e>X>>>)>X<*>T<N<f>X>>>>)>X<*>>>")
    }

    test(expected = javaClass<ParserException>()) fun testFail1() {
        Parser.parse("abc(|")
    }

    test(expected = javaClass<ParserException>()) fun testFail2() {
        Parser.parse("asdf||")
    }

    test(expected = javaClass<ParserException>()) fun testFail3() {
        Parser.parse("|x")
    }

    test(expected = javaClass<ParserException>()) fun testFail4() {
        Parser.parse("x**")
    }
}