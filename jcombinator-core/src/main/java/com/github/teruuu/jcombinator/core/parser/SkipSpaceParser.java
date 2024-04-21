package com.github.teruuu.jcombinator.core.parser;

public class SkipSpaceParser implements Parser<Void> {
    @Override
    public ParseResult<Void> parse(String input, ParserContext context) {
        char c;
        int location = context.location();
        while (true) {
            if (input.length() == location) {
                break;
            }

            c = input.charAt(location);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                location++;
            } else {
                break;
            }
        }
        return new ParseResult.Success<>(null, context.nextLocation(location));
    }
}
