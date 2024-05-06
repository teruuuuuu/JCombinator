package com.github.teruuu.jcombinator.regexp.vm;

import com.github.teruuu.jcombinator.core.parser.ParseContext;
import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.type.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegExpParserTest {


    @Test
    public void test() {
        Parser<Rule> parser = new RegExpParser();
        Tuple<ParseContext, ParseResult<Rule>> parseResultState = parser.parse("abc");
        ParseResult<Rule> parseResult = parseResultState._2();

        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        Rule rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.cons(Rule.cons(Rule.literal('a'), Rule.literal('b')), Rule.literal('c')));
        RegExp regExp = new RegExp(rule);
        RegExpResult regExpResult = regExp.search("abc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        RegExpResult.Success success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 3)));

        parseResultState = parser.parse("(ab)*");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.zeroSeq(Rule.cons(Rule.literal('a'), Rule.literal('b'))));
        regExp = new RegExp(rule);
        regExpResult = regExp.search("abc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 2), Location.of(2, 2), Location.of(3, 3)));

        parseResultState = parser.parse("abc+|d+");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.select(
                Rule.cons(
                        Rule.cons(
                                Rule.literal('a'),
                                Rule.literal('b')
                        ),
                        Rule.oneSeq(Rule.literal('c'))
                ),
                Rule.oneSeq(Rule.literal('d'))
        ));
        regExp = new RegExp(rule);
        regExpResult = regExp.search("abcccccabdddd");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 7), Location.of(9, 13)));


        parseResultState = parser.parse(".*hoge.*");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.cons(
                        Rule.cons(
                                Rule.cons(
                                        Rule.cons(
                                                Rule.cons(
                                                        Rule.zeroSeq(Rule.any()),
                                                        Rule.literal('h')
                                                ),
                                                Rule.literal('o')
                                        ),
                                        Rule.literal('g')
                                ),
                                Rule.literal('e')
                        ),
                        Rule.zeroSeq(Rule.any())
                )
        );
        regExp = new RegExp(rule);
        regExpResult = regExp.search("abcdefghogeabcdefg");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 18)));


        parseResultState = parser.parse("apple|banana");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.select(
                        Rule.cons(
                                Rule.cons(
                                        Rule.cons(
                                                Rule.cons(
                                                        Rule.literal('a'),
                                                        Rule.literal('p')
                                                ),
                                                Rule.literal('p')
                                        ),
                                        Rule.literal('l')
                                ),
                                Rule.literal('e')
                        ),
                        Rule.cons(
                                Rule.cons(
                                        Rule.cons(
                                                Rule.cons(
                                                        Rule.cons(
                                                                Rule.literal('b'),
                                                                Rule.literal('a')
                                                        ),
                                                        Rule.literal('n')
                                                ),
                                                Rule.literal('a')
                                        ),
                                        Rule.literal('n')
                                ),
                                Rule.literal('a')
                        )
                )
        );
        parseResultState = parser.parse("(apple|banana)");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.select(
                        Rule.cons(
                                Rule.cons(
                                        Rule.cons(
                                                Rule.cons(
                                                        Rule.literal('a'),
                                                        Rule.literal('p')
                                                ),
                                                Rule.literal('p')
                                        ),
                                        Rule.literal('l')
                                ),
                                Rule.literal('e')
                        ),
                        Rule.cons(
                                Rule.cons(
                                        Rule.cons(
                                                Rule.cons(
                                                        Rule.cons(
                                                                Rule.literal('b'),
                                                                Rule.literal('a')
                                                        ),
                                                        Rule.literal('n')
                                                ),
                                                Rule.literal('a')
                                        ),
                                        Rule.literal('n')
                                ),
                                Rule.literal('a')
                        )
                )
        );
        regExp = new RegExp(rule);
        regExpResult = regExp.search("apple_apple_banana_banana");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 5), Location.of(6, 11), Location.of(12, 18), Location.of(19, 25)));

        parseResultState = parser.parse("^apple|banana$");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.select(
                        Rule.cons(
                                Rule.cons(
                                        Rule.cons(
                                                Rule.cons(
                                                        Rule.cons(
                                                                Rule.head(),
                                                                Rule.literal('a')
                                                        ),
                                                        Rule.literal('p')
                                                ),
                                                Rule.literal('p')
                                        ),
                                        Rule.literal('l')
                                ),
                                Rule.literal('e')
                        ),
                        Rule.cons(
                                Rule.cons(
                                        Rule.cons(
                                                Rule.cons(
                                                        Rule.cons(
                                                                Rule.cons(
                                                                        Rule.literal('b'),
                                                                        Rule.literal('a')
                                                                ),
                                                                Rule.literal('n')
                                                        ),
                                                        Rule.literal('a')
                                                ),
                                                Rule.literal('n')
                                        ),
                                        Rule.literal('a')
                                ),
                                Rule.end()
                        )
                )
        );
        regExp = new RegExp(rule);
        regExpResult = regExp.search("apple_apple_banana_banana");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 5), Location.of(19, 25)));


        parseResultState = parser.parse("[a-zA-Z-]");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.select(Rule.select(Rule.range('a', 'z'), Rule.range('A', 'Z')), Rule.literal('-')));
        regExp = new RegExp(rule);
        regExpResult = regExp.search("abc_012_XYZ-");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 1), Location.of(1, 2), Location.of(2, 3),
                Location.of(8, 9), Location.of(9, 10), Location.of(10, 11), Location.of(11, 12)));

        parseResultState = parser.parse("[^a-zA-Z!]");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.not(Rule.select(Rule.select(Rule.range('a', 'z'), Rule.range('A', 'Z')), Rule.literal('!'))));
        regExp = new RegExp(rule);
        regExpResult = regExp.search("abc_012_XYZ!");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(3, 4), Location.of(4, 5), Location.of(5, 6), Location.of(6, 7), Location.of(7, 8)));

        parseResultState = parser.parse("a{3}");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.quantity(Rule.literal('a'), 3));
        regExp = new RegExp(rule);
        regExpResult = regExp.search("aaaaaaaa");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 3), Location.of(3, 6)));

        parseResultState = parser.parse("a{,3}");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.quantityLess(Rule.literal('a'), 3));
        regExp = new RegExp(rule);
        regExpResult = regExp.search("aaaaaaaa");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 3), Location.of(3, 6), Location.of(6, 8), Location.of(8, 8)));

        parseResultState = parser.parse("x(?=abc)");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Rule>);
        rule = ((ParseResult.Success<Rule>) parseResult).value();
        assertEquals(rule, Rule.cons(
                Rule.literal('x'),
                Rule.lookAhead(
                        Rule.cons(
                                Rule.cons(
                                        Rule.literal('a'),
                                        Rule.literal('b')
                                ),
                                Rule.literal('c')
                        )
                )
        ));
        regExp = new RegExp(rule);
        regExpResult = regExp.search("xabcxxabcx");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 1), Location.of(5, 6)));


    }
}
