package com.github.teruuu.jcombinator.core.parser;

public class SkipParser implements Parser<Void> {
    private final String literal;

    public SkipParser(String literal) {
        this.literal = literal;
    }

    @Override
    public ParseResult<Void> parse(String input, ParserContext context) {
        if (input.startsWith(literal, context.location())) {
            return new ParseResult.Success<>(null, context.moveLocation(literal.length()));
        } else {
            return new ParseResult.Failure<>(context.newError(String.format("not (literal=[%s], loc=[%d]), input=%s", literal, context.location(), input)));
        }
    }
}
