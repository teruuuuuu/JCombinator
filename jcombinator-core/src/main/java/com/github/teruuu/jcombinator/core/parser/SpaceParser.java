package com.github.teruuu.jcombinator.core.parser;

public class SpaceParser implements Parser<Void> {
    @Override
    public ParseResult<Void> parse(String input, ParserContext context) {
        int location = context.location();
        if (input.length() > location) {
            char c1 = input.charAt(location);
            if (c1 == ' ' || c1 == '\t' || c1 == '\n') {
                return new ParseResult.Success<>(null, context.moveLocation(1));
            } else if (c1 == '\r' && input.length() > location + 1) {
                char c2 = input.charAt(location + 1);
                if (c2 == '\n') {
                    return new ParseResult.Success<>(null, context.moveLocation(2));
                } else {
                    return new ParseResult.Failure<>(context.newError(String.format("not space location=[%d]", location)));
                }
            } else {
                return new ParseResult.Failure<>(context.newError(String.format("not space location=[%d]", location)));
            }
        } else {
            return new ParseResult.Failure<>(context.newError(String.format("not space location=[%d]", location)));
        }
    }
}
