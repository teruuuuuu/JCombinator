package com.github.teruuu.jcombinator.core.parser;

public class StringParser implements Parser<String> {
    final String literal;

    public StringParser(String literal) {
        this.literal = literal;
    }

    @Override
    public ParseResult<String> parse(String input, ParserContext context) {
        if (input.startsWith(literal, context.location())) {
            return new ParseResult.Success<>(literal, context.moveLocation(literal.length()));
        } else {
            return new ParseResult.Failure<>(context.newError(String.format("not (literal=[%s], loc=[%d]), input=%s", literal, context.location(), input)));
        }
    }
}
