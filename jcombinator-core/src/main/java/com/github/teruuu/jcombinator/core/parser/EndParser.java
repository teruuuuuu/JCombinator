package com.github.teruuu.jcombinator.core.parser;

import java.util.List;

public class EndParser implements Parser<Void> {
    @Override
    public ParseResult<Void> parse(String input, int location) {
        if (input.length() == location) {
            return new ParseResult.Success<>(null, location);
        } else {
            return new ParseResult.Failure<>(new ParseError("end", "not end", location, List.of()), location);
        }
    }
}
