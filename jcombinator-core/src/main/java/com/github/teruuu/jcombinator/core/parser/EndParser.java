package com.github.teruuu.jcombinator.core.parser;

public class EndParser implements Parser<Void> {
    @Override
    public ParseResult<Void> parse(String input, ParserContext context) {
        if (input.length() == context.location()) {
            return new ParseResult.Success<>(null, context);
        } else {
            return new ParseResult.Failure<>(context.newError(String.format("not eof expected length=[%d] actual=[%d]", context.location(), input.length())));
        }
    }
}
