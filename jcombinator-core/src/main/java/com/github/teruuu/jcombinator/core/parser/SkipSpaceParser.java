package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class SkipSpaceParser implements Parser<Void> {
    @Override
    public Tuple<ParseContext, ParseResult<Void>> parse(String input, ParseContext context) {
        int location = context.location();
        char c;
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
        return new Tuple<>(context.newLocation(location), new ParseResult.Success<>(null));
    }
}
