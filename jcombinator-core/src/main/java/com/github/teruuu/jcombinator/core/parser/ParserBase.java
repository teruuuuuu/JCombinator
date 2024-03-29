package com.github.teruuu.jcombinator.core.parser;

public abstract class ParserBase<T> implements Parser<T> {
    Parser<T> parser;

    protected abstract Parser<T> genParser();

    @Override
    public ParseResult<T> parse(String input, int location) {
        if (parser == null) {
            this.parser = genParser();
        }
        return parser.parse(input, location);
    }
}