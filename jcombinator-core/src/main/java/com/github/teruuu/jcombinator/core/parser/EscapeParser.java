package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class EscapeParser implements Parser<String> {
    @Override
    public Tuple<ParseContext, ParseResult<String>> parse(String input, ParseContext context) {
//        int location = context.location();
        if (input.length() >= context.location() + 1 && input.startsWith("\\", context.location())) {
            char c = input.charAt(context.location() + 1);
            if (c == 't') {
                return new Tuple<>(context.move(2), new ParseResult.Success<>(String.valueOf('\t')));
            } else if (c == 'f') {
                return new Tuple<>(context.move(2), new ParseResult.Success<>(String.valueOf('\f')));
            } else if (c == 'b') {
                return new Tuple<>(context.move(2), new ParseResult.Success<>(String.valueOf('\b')));
            } else if (c == 'r') {
                return new Tuple<>(context.move(2), new ParseResult.Success<>(String.valueOf('\r')));
            } else if (c == 'n') {
                return new Tuple<>(context.move(2), new ParseResult.Success<>(String.valueOf('\n')));
            } else if (c == '\\') {
                return new Tuple<>(context.move(2), new ParseResult.Success<>(String.valueOf('\\')));
            } else if (c == '"') {
                return new Tuple<>(context.move(2), new ParseResult.Success<>(String.valueOf('"')));
            } else if (c == '\'') {
                return new Tuple<>(context.move(2), new ParseResult.Success<>(String.valueOf('\'')));
            }
        }
        return new Tuple<>(context.newError("escape", String.format("not escape location=[%d] input=[%s]", context.location(), input)),
                new ParseResult.Failure<>());
    }
}
