package com.github.teruuu.jcombinator.core.parser;

public class DQuoteStringParser implements Parser<String> {
    private final Parser<String> parser;

    public DQuoteStringParser() {
        this.parser = Parser.literal('"').andRight(
                Parser.escape().or(Parser.literal('"').not()).seq0().map(value -> String.join("", value))
        ).andLeft(Parser.literal('"'));
    }

    @Override
    public ParseResult<String> parse(String input, int location) {
        return parser.parse(input, location);
    }
}
