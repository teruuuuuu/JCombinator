package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class SkipCharParser implements Parser<Void> {
    final char c;

    public SkipCharParser(char c) {
        this.c = c;
    }

    @Override
    public Tuple<ParseContext, ParseResult<Void>> parse(String input, ParseContext context) {
        if (input.length() > context.location() && input.charAt(context.location()) == c) {
            return new Tuple<>(context.move(1), new ParseResult.Success<>(null));
        } else {
            return new Tuple<>(
                    context.newError(
                            "skipChar",
                            String.format("not (char=[%s], loc=[%d]), input=%s", c, context.location(), input)),
                    new ParseResult.Failure<>());
        }
    }
}
