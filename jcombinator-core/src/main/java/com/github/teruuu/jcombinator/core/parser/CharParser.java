package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class CharParser implements Parser<String> {

    final char c;

    public CharParser(char c) {
        this.c = c;
    }

    @Override
    public Tuple<ParseContext, ParseResult<String>> parse(String input, ParseContext context) {
        if (input.length() > context.location() && input.charAt(context.location()) == c) {
            return new Tuple<>(context.move(1), new ParseResult.Success<>(String.valueOf(c)));
        } else {
            return new Tuple<>(
                    context.newError("char", String.format("not (char=[%s], loc=[%d]), input=%s", c, context.location(), input)),
                    new ParseResult.Failure<>()
            );
        }
    }
}
