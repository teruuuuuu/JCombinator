package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class StringParser implements Parser<String> {
    final String literal;

    public StringParser(String literal) {
        this.literal = literal;
    }

    @Override
    public Tuple<ParseContext, ParseResult<String>> parse(String input, ParseContext context) {
        if (input.startsWith(literal, context.location())) {
            return new Tuple<>(context.move(literal.length()), new ParseResult.Success<>(literal));
        } else {
            return new Tuple<>(
                    context.newError(
                            "string",
                            String.format("not (literal=[%s], loc=[%d]), input=%s", literal, context.location(), input)),
                    new ParseResult.Failure<>());
        }
    }
}
