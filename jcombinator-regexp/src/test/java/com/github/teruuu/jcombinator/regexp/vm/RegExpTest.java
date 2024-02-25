package com.github.teruuu.jcombinator.regexp.vm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class RegExpTest {

    @Test
    public void test() {
        RegExp regExp = new RegExp(new Rule.Literal('a'));
        RegExpResult regExpResult = regExp.search("abc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        RegExpResult.Success success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 1)));

        regExp = new RegExp(new Rule.Cons(new Rule.Literal('a'), new Rule.Literal('b')));
        regExpResult = regExp.search("abc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 2)));

        regExp = new RegExp(new Rule.Select(new Rule.Literal('a'), new Rule.Literal('b')));
        regExpResult = regExp.search("abc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 1), Location.of(1, 2)));

        regExp = new RegExp(new Rule.Cons(new Rule.Any(), new Rule.Literal('b')));
        regExpResult = regExp.search("abc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 2)));

        regExp = new RegExp(new Rule.Cons(new Rule.Option(new Rule.Literal('a')), new Rule.Any()));
        regExpResult = regExp.search("abc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 2), Location.of(2, 3)));

        regExp = new RegExp(new Rule.Cons(new Rule.Option(new Rule.Literal('z')), new Rule.Any()));
        regExpResult = regExp.search("abc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 1), Location.of(1, 2), Location.of(2, 3)));

        regExp = new RegExp(new Rule.ZeroSeq(
                new Rule.Select(
                        new Rule.Literal('a'),
                        new Rule.Literal('b'))));
        regExpResult = regExp.search("aaabbbccc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(List.of(
                Location.of(0, 6), Location.of(6, 6), Location.of(7, 7), Location.of(8, 8), Location.of(9, 9)
        )));


        regExp = new RegExp(new Rule.Cons(
                new Rule.Head(),
                new Rule.Cons(
                        new Rule.OneSeq(
                                new Rule.Select(
                                        new Rule.Literal('a'),
                                        new Rule.Literal('b'))
                        ),
                        new Rule.Cons(
                                new Rule.ZeroSeq(
                                        new Rule.Literal('c')
                                ),
                                new Rule.End()
                        )
                )
        ));
        regExpResult = regExp.search("aaabbbccc");
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 9)));


        // ^.*(?=.*hoge.*)(?=.*fuga.*).*$
        regExp = RegExp.of(Rule.cons(
                Rule.head(),
                Rule.cons(
                        Rule.zeroSeq(Rule.any()),
                        Rule.cons(
                                Rule.lookAhead(
                                        Rule.cons(
                                                Rule.zeroSeq(Rule.any()),
                                                Rule.cons(
                                                        Rule.cons(
                                                                Rule.literal('h'),
                                                                Rule.cons(
                                                                        Rule.literal('o'),
                                                                        Rule.cons(
                                                                                Rule.literal('g'),
                                                                                Rule.literal('e')
                                                                        )
                                                                )
                                                        ),
                                                        Rule.zeroSeq(Rule.any())
                                                )
                                        )
                                ),
                                Rule.cons(
                                        Rule.lookAhead(
                                                Rule.cons(
                                                        Rule.zeroSeq(Rule.any()),
                                                        Rule.cons(
                                                                Rule.cons(
                                                                        Rule.literal('f'),
                                                                        Rule.cons(
                                                                                Rule.literal('u'),
                                                                                Rule.cons(
                                                                                        Rule.literal('g'),
                                                                                        Rule.literal('a')
                                                                                )
                                                                        )
                                                                ),
                                                                Rule.zeroSeq(Rule.any())
                                                        )
                                                )
                                        ),
                                        Rule.cons(
                                                Rule.zeroSeq(Rule.any()),
                                                Rule.end()
                                        )
                                )
                        )
                )
        ));
        String input = "aaaahogeaaaafugaaaaaa";
        regExpResult = regExp.search(input);
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, input.length())));

        input = "aaaaahogeaaaaa";
        regExpResult = regExp.search(input);
        assertInstanceOf(RegExpResult.Failed.class, regExpResult);

        // ^.*(?=.*hoge.*)
        regExp = RegExp.of(Rule.cons(
                Rule.head(),
                Rule.cons(
                        Rule.zeroSeq(Rule.any()),
                        Rule.lookAhead(
                                Rule.cons(
                                        Rule.zeroSeq(Rule.any()),
                                        Rule.cons(
                                                Rule.cons(
                                                        Rule.literal('h'),
                                                        Rule.cons(
                                                                Rule.literal('o'),
                                                                Rule.cons(
                                                                        Rule.literal('g'),
                                                                        Rule.literal('e')
                                                                )
                                                        )
                                                ),
                                                Rule.zeroSeq(Rule.any())
                                        )
                                )
                        )
                )
        ));
        input = "aaaaahogeaaaaa";
        regExpResult = regExp.search(input);
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, input.indexOf("hoge"))));

        // ^.*(?<=hoge)
        regExp = RegExp.of(Rule.cons(
                Rule.head(),
                Rule.cons(
                        Rule.zeroSeq(Rule.any()),
                        Rule.lookBehind(
                                Rule.cons(
                                        Rule.literal('h'),
                                        Rule.cons(
                                                Rule.literal('o'),
                                                Rule.cons(
                                                        Rule.literal('g'),
                                                        Rule.literal('e')
                                                )
                                        )
                                )
                        )
                )
        ));
        input = "aaaaahogeaaaaa";
        regExpResult = regExp.search(input);
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 9)));


        // ^(?!.*hoge).*$
        regExp = RegExp.of(Rule.cons(
                Rule.cons(
                        Rule.cons(
                                Rule.head(),
                                Rule.notLookAhead(
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
                                        )
                                )
                        ),
                        Rule.zeroSeq(Rule.any())
                ),
                Rule.end()
        ));
        input = "aaaaahogeaaaaa";
        regExpResult = regExp.search(input);
        assertInstanceOf(RegExpResult.Failed.class, regExpResult);

        input = "aaaaahogaaaaa";
        regExpResult = regExp.search(input);
        assertInstanceOf(RegExpResult.Success.class, regExpResult);
        success = (RegExpResult.Success) regExpResult;
        assertEquals(success, RegExpResult.success(Location.of(0, 13)));
    }
}
