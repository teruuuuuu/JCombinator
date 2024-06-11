package com.github.teruuu.jcombinator.core.parser;

import java.util.ArrayList;
import java.util.List;

public class Seq1Parser<T> implements Parser<List<T>> {
    private final Parser<T> parser;

    public Seq1Parser(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public ParseResult<List<T>> parse(String input, int location) {
        List<T> ret = new ArrayList<>();
        while (true) {
            ParseResult<T> parseResult = parser.parse(input, location);
            if (parseResult instanceof ParseResult.Success<T> success) {
                ret.add(success.value());
                location = success.location();
            } else {
                break;
            }
        }
        if (!ret.isEmpty()) {
            return new ParseResult.Success<>(ret, location);
        } else {
            return new ParseResult.Failure<>(new ParseError("seq1", "", location, List.of()), location);
        }
    }
}
