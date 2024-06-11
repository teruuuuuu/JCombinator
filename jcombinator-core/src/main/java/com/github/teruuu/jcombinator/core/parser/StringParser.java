package com.github.teruuu.jcombinator.core.parser;

import java.util.List;

public class StringParser implements Parser<String> {
    final String literal;

    public StringParser(String literal) {
        this.literal = literal;
    }

    @Override
    public ParseResult<String> parse(String input, int location) {
        if (input.startsWith(literal, location)) {
            return new ParseResult.Success<>(literal, location + literal.length());
        } else {
            return new ParseResult.Failure<>(
                    new ParseError("string", String.format("not (literal=[%s], loc=[%d]), input=%s", literal, location, input), location, List.of()),
                    location);
        }
    }
}
