package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class StopWardParser implements Parser<String> {
    private final String literal;

    public StopWardParser(String literal) {
        this.literal = literal;
    }

    @Override
    public Tuple<ParseContext, ParseResult<String>> parse(String input, ParseContext context) {
        for (int i = context.location(); i < input.length(); i++) {
            if (input.startsWith(literal, i)) {
                return new Tuple<>(context.newLocation(i), new ParseResult.Success<>(input.substring(context.location(), i)));
            }
        }

        return new Tuple<>(
                context.newError(
                        "stopWard",
                        String.format("not find literal=[%s], location=[%d] input=[%s]", literal, context.location(), input)),
                new ParseResult.Failure<>()
        );
    }
}
