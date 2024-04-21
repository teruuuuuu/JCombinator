package com.github.teruuu.jcombinator.core.parser;

import java.util.ArrayList;
import java.util.List;

public class Seq1Parser<T> implements Parser<List<T>> {
    private final Parser<T> parser;

    public Seq1Parser(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public ParseResult<List<T>> parse(String input, ParserContext context) {
        List<T> ret = new ArrayList<>();
        ParserContext nextContext = context;
        while (true) {
            ParseResult<T> parseResult = parser.parse(input, nextContext);
            if (parseResult instanceof ParseResult.Success<T> success) {
                ret.add(success.value());
                nextContext = success.context();
            } else {
                break;
            }
        }
        if (!ret.isEmpty()) {
            return new ParseResult.Success<>(ret, nextContext);
        } else {
            return new ParseResult.Failure<>(nextContext);
        }
    }
}
