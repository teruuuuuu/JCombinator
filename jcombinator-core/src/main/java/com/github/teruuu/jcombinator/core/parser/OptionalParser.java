package com.github.teruuu.jcombinator.core.parser;

import java.util.Optional;

public class OptionalParser<T> implements Parser<Optional<T>> {
    private final Parser<T> parser;

    public OptionalParser(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public ParseResult<Optional<T>> parse(String input, ParserContext context) {
        switch (parser.parse(input, context)) {
            case ParseResult.Success<T> success -> {
                return new ParseResult.Success<>(Optional.of(success.value()), success.context());
            }
            case ParseResult.Failure<T> failure -> {
                return new ParseResult.Success<>(Optional.empty(), context);
            }
        }
    }
}
