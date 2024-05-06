package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class RangeParser implements Parser<String> {
    final char c1;
    final char c2;

    public RangeParser(char c1, char c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public Tuple<ParseContext, ParseResult<String>> parse(String input, ParseContext context) {
        if (input.length() > context.location()) {
            char c = input.charAt(context.location());
            if (c >= c1 && c <= c2) {
                return new Tuple<>(context.move(1), new ParseResult.Success<>(String.valueOf(c)));
            } else {
                return new Tuple<>(context.newError("range", "not range"), new ParseResult.Failure<>());
            }
        } else {
            return new Tuple<>(context.newError("range", "reach end"), new ParseResult.Failure<>());
        }
    }
}
