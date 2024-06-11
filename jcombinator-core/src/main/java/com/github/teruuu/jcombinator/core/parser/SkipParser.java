package com.github.teruuu.jcombinator.core.parser;

import java.util.List;

public class SkipParser implements Parser<Void> {
    private final String literal;

    public SkipParser(String literal) {
        this.literal = literal;
    }

    @Override
    public ParseResult<Void> parse(String input, int location) {
        if (input.startsWith(literal, location)) {
            return new ParseResult.Success<>(null, location + literal.length());
        } else {
            return new ParseResult.Failure<>(
                    new ParseError("skip", String.format("not (literal=[%s], loc=[%d]), input=%s", literal, location, input), location, List.of()),
                    location);
        }
    }
}
