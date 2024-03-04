package com.github.teruuu.parser;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RangeParserTest {

    @Test
    public void test() {
        Parser<String> parer = (
                Parser.range('a', 'z').or(Parser.range('A', 'Z'))
        ).and(
                Parser.range('a', 'z').or(Parser.range('A', 'Z')).or(Parser.range('0','9')).or(Parser.literal('_')).seq0()
        ).map(e -> e._1() + String.join("", e._2())).withSkipSpace();

        ParseResult<String> parseResult = parer.parse("def main123_(){}", 3);
        assertTrue(parseResult instanceof ParseResult.Success<String>);
        String value = ((ParseResult.Success<String>) parseResult).value();
        assertEquals("main123_", value);

    }
}
