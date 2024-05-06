package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

import java.util.Optional;

public class OptionalParser<T> implements Parser<Optional<T>> {
    private final Parser<T> parser;

    public OptionalParser(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public Tuple<ParseContext, ParseResult<Optional<T>>> parse(String input, ParseContext context) {
        Tuple<ParseContext, ParseResult<T>> fParseResultState = parser.parse(input, context);
        ParseContext newContext = fParseResultState._1();
        ParseResult<T> parseResult = fParseResultState._2();

        switch (parseResult) {
            case ParseResult.Success<T> success -> {
                return new Tuple<>(
                        newContext,
                        new ParseResult.Success<>(Optional.of(success.value())));
            }
            case ParseResult.Failure<T> failure -> {
                return new Tuple<>(
                        context.newError("optional", "not valid parser").addError(newContext),
                        new ParseResult.Success<>(Optional.empty())
                );
            }
        }
    }
}
