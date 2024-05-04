package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public abstract class ParserBase<T> implements Parser<T> {
    Parser<T> parser;
    protected abstract Parser<T> genParser();

    private String label;
    private String message;

    public ParserBase() {

    }
    public ParserBase(String label, String message) {
        this.label = label;
        this.message = message;
    }

    @Override
    public Tuple<ParseContext, ParseResult<T>> parse(String input, ParseContext context) {
        if (parser == null) {
            if (label != null) {
                this.parser = genParser().labeled(label, message);
            } else {
                this.parser = genParser();
            }
        }
        return parser.parse(input, context);
    }
}