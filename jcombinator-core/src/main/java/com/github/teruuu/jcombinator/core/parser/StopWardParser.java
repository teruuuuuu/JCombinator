package com.github.teruuu.jcombinator.core.parser;

import java.util.List;

public class StopWardParser implements Parser<String> {
    private final String literal;

    public StopWardParser(String literal) {
        this.literal = literal;
    }

    @Override
    public ParseResult<String> parse(String input, int location) {
        for (int i = location; i < input.length(); i++) {
            if (input.startsWith(literal, i)) {
                return new ParseResult.Success<>(input.substring(location, i), i);
            }
        }
        return new ParseResult.Failure<>(
                new ParseError("stopWard", String.format("not find literal=[%s], location=[%d] input=[%s]", literal, location, input), location, List.of()),
                location);
    }
}
