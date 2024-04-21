package com.github.teruuu.jcombinator.core.parser;

public class NotParser<T> implements Parser<String> {
    private final Parser<T> parser;

    public NotParser(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public ParseResult<String> parse(String input, ParserContext context) {
        if (input.length() == context.location()) {
            return new ParseResult.Failure<>(context.newError("reached end."));
        } else {
            switch (parser.parse(input, context)) {
                case ParseResult.Failure<T> a -> {
                    return new ParseResult.Success<>(input.substring(context.location(), context.location() + 1), context.moveLocation(1));
                }
                case ParseResult.Success<T> success -> {
                    return new ParseResult.Failure<>(context.newError(""));
                }
            }
        }
    }
}
