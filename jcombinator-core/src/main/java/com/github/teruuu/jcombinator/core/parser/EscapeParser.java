package com.github.teruuu.jcombinator.core.parser;

public class EscapeParser implements Parser<String> {
    @Override
    public ParseResult<String> parse(String input, ParserContext context) {
        if (input.length() >= context.location() + 1 && input.startsWith("\\", context.location())) {
            char c = input.charAt(context.location() + 1);
            if (c == 't') {
                return new ParseResult.Success<>(String.valueOf('\t'), context.moveLocation(2));
            } else if (c == 'f') {
                return new ParseResult.Success<>(String.valueOf('\f'), context.moveLocation(2));
            } else if (c == 'b') {
                return new ParseResult.Success<>(String.valueOf('\b'), context.moveLocation(2));
            } else if (c == 'r') {
                return new ParseResult.Success<>(String.valueOf('\r'), context.moveLocation(2));
            } else if (c == 'n') {
                return new ParseResult.Success<>(String.valueOf('\n'), context.moveLocation(2));
            } else if (c == '\\') {
                return new ParseResult.Success<>(String.valueOf('\\'), context.moveLocation(2));
            } else if (c == '"') {
                return new ParseResult.Success<>(String.valueOf('"'), context.moveLocation(2));
            } else if (c == '\'') {
                return new ParseResult.Success<>(String.valueOf('\''), context.moveLocation(2));
            }
        }
        return new ParseResult.Failure<>(context.newError(String.format("not escape location=[%d] input=[%s]", context.location(), input)));
    }
}
