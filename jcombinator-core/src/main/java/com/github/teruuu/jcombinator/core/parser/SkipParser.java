package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class SkipParser implements Parser<Void> {
    private final String literal;

    public SkipParser(String literal) {
        this.literal = literal;
    }

    @Override
    public Tuple<ParseContext, ParseResult<Void>> parse(String input, ParseContext context) {
        if (input.startsWith(literal, context.location())) {
            return new Tuple<>(context.move(literal.length()), new ParseResult.Success<>(null));
        } else {
            return new Tuple<>(
                    context.newError(
                            "skip",
                            String.format("not (literal=[%s], loc=[%d]), input=%s", literal, context.location(), input))
                    , new ParseResult.Failure<>());
        }
    }
}
