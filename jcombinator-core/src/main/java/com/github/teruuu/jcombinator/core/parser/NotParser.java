package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class NotParser<T> implements Parser<String> {
    private final Parser<T> parser;

    public NotParser(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public Tuple<ParseContext, ParseResult<String>> parse(String input, ParseContext context) {
        if (input.length() == context.location()) {
            return new Tuple<>(context.newError("not", "reach end"), new ParseResult.Failure<>());
        } else {
            Tuple<ParseContext, ParseResult<T>> fParseResultState = parser.parse(input, context);
            ParseContext newContext = fParseResultState._1();
            ParseResult<T> parseResult = fParseResultState._2();
            switch (parseResult) {
                case ParseResult.Failure<T> a -> {
                    return new Tuple<>(context.move(1), new ParseResult.Success<>(input.substring(context.location(), context.location() + 1)));
                }
                case ParseResult.Success<T> success -> {
                    return new Tuple<>(context.newError("not", "parse success"), new ParseResult.Failure<>());
                }
            }
        }
    }
}
