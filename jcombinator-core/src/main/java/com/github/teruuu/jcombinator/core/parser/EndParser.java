package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class EndParser implements Parser<Void> {
    @Override
    public Tuple<ParseContext, ParseResult<Void>> parse(String input, ParseContext context) {
        if (input.length() == context.location()) {
            return new Tuple<>(context.move(0), new ParseResult.Success<>(null));
        } else {
            return new Tuple<>(
                    context.newError("end", String.format("not eof expected length=[%d] actual=[%d]", context.location(), input.length())),
                    new ParseResult.Failure<>());
        }
    }
}
