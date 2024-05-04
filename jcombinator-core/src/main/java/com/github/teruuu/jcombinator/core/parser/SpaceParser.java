package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class SpaceParser implements Parser<Void> {
    @Override
    public Tuple<ParseContext, ParseResult<Void>> parse(String input, ParseContext context) {
        int location = context.location();
        if (input.length() > location) {
            char c1 = input.charAt(location);
            if (c1 == ' ' || c1 == '\t' || c1 == '\n') {
                return new Tuple<>(context.move(1), new ParseResult.Success<>(null));
            } else if (c1 == '\r' && input.length() > location + 1) {
                char c2 = input.charAt(location + 1);
                if (c2 == '\n') {
                    return new Tuple<>(context.move(2), new ParseResult.Success<>(null));
                } else {
                    return new Tuple<>(context.newError("space", "not space"), new ParseResult.Failure<>());
                }
            } else {
                return new Tuple<>(context.newError("space", "not space"), new ParseResult.Failure<>());
            }
        } else {
            return new Tuple<>(context.newError("space", "not space"), new ParseResult.Failure<>());
        }
    }
}
