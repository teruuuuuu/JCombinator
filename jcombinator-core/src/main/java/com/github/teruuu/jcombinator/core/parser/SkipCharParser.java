package com.github.teruuu.jcombinator.core.parser;

import java.util.List;

public class SkipCharParser implements Parser<Void> {
    final char c;

    public SkipCharParser(char c) {
        this.c = c;
    }

    @Override
    public ParseResult<Void> parse(String input, int location) {
        if (input.length() > location && input.charAt(location) == c) {
            return new ParseResult.Success<>(null, location + 1);
        } else {
            return new ParseResult.Failure<>(
                    new ParseError("skipChar", String.format("not (char=[%s], loc=[%d]), input=%s", c, location, input), location, List.of()),
                    location);
        }
    }
}
