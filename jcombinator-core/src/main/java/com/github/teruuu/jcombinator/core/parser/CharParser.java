package com.github.teruuu.jcombinator.core.parser;

public class CharParser implements Parser<String> {

    final char c;

    public CharParser(char c) {
        this.c = c;
    }

    @Override
    public ParseResult<String> parse(String input, ParserContext context) {
        if (input.length() > context.location() && input.charAt(context.location()) == c) {
            return new ParseResult.Success<>(String.valueOf(c), context.nextLocation(context.location() + 1));
        } else {
            String message = String.format("not (char=[%s], loc=[%d]), input=%s", c, context.location(), input);


            return new ParseResult.Failure<>(context.newError(context.location(), message));
        }
    }
}
