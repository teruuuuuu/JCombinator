package com.github.teruuu.jcombinator.core.parser;

import java.util.List;

public class RangeParser implements Parser<String> {
    final char c1;
    final char c2;

    public RangeParser(char c1, char c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public ParseResult<String> parse(String input, int location) {
        if (input.length() > location) {
            char c = input.charAt(location);
            if (c >= c1 && c <= c2) {
                return new ParseResult.Success<>(String.valueOf(c), location + 1);
            } else {
                return new ParseResult.Failure<>(
                        new ParseError("range", String.format("range over %s >= %s && %s <= %s", c, c1, c, c2), location, List.of()),
                        location);
            }
        } else {
            return new ParseResult.Failure<>(
                    new ParseError("range", "length over", location, List.of()), location);
        }
    }
}
