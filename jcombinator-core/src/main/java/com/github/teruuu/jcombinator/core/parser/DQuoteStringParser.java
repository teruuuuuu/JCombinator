package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class DQuoteStringParser implements Parser<String> {
    private final Parser<String> parser;

    public DQuoteStringParser() {
        this.parser = Parser.literal('"').andRight(
                Parser.escape().or(Parser.literal('"').not()).seq0().map(value -> String.join("", value))
        ).andLeft(Parser.literal('"'));
    }

    @Override
    public Tuple<ParseContext, ParseResult<String>> parse(String input, ParseContext context) {
        return parser.parse(input, context);
    }
}
