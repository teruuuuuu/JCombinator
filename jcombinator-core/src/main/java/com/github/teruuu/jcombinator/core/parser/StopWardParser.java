package com.github.teruuu.jcombinator.core.parser;

public class StopWardParser implements Parser<String> {
    private final String literal;

    public StopWardParser(String literal) {
        this.literal = literal;
    }

    @Override
    public ParseResult<String> parse(String input, ParserContext context) {
        for (int i = context.location(); i < input.length(); i++) {
            if (input.startsWith(literal, i)) {
                return new ParseResult.Success<>(input.substring(context.location(), i), context.nextLocation(i));
            }
        }
        return new ParseResult.Failure<>(context.newError(String.format("not find literal=[%s], location=[%d] input=[%s]", literal, context.location(), input)));
    }
}
