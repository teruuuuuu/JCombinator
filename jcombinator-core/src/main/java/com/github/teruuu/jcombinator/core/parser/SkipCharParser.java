package com.github.teruuu.jcombinator.core.parser;

public class SkipCharParser implements Parser<Void> {
    final char c;

    public SkipCharParser(char c) {
        this.c = c;
    }

    @Override
    public ParseResult<Void> parse(String input, ParserContext context) {
        if (input.length() > context.location() && input.charAt(context.location()) == c) {
            return new ParseResult.Success<>(null, context.moveLocation(1));
        } else {
            return new ParseResult.Failure<>(context.newError(String.format("not (char=[%s], loc=[%d]), input=%s", c, context.location(), input)));
        }
    }
}
