package com.github.teruuu.jcombinator.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ParserContext {
    private int location;

    private ParseError lastError;
    private List<ParseError> errors;

    public ParserContext() {
        this.location = 0;
        this.lastError = null;
        this.errors = new ArrayList<>();
    }

    public ParserContext(int location) {
        this.location = location;
        this.lastError = null;
        this.errors = new ArrayList<>();
    }

    private ParserContext(int location, ParseError lastError, List<ParseError> errors) {
        this.location = location;
        this.lastError = lastError;
        this.errors = errors;
    }

    public static ParserContext init() {
        return new ParserContext();
    }

    public static ParserContext init(int location) {
        return new ParserContext(location);
    }

    public int location() {
        return location;
    }

    public ParseError lastError() {
        return this.lastError;
    }
    public List<String> errors() {
        return errors.stream().sorted((a,b) -> b.location() - a.location())
                .map(ParseError::message).toList();
    }

    public ParserContext moveLocation(int move) {
        return nextLocation(location + move);
    }
    public ParserContext nextLocation(int location) {
        return new ParserContext(location, lastError, errors);
    }

    public ParserContext newError(String message) {
        return newError(new ParseError(location, message));
    }

    public ParserContext newError(int location, String message) {
        return newError(new ParseError(location, message));
    }

    public ParserContext newError(ParseError parseError) {

        return new ParserContext(
                this.location,
                parseError,
                parseError == null ? this.errors : Stream.concat(this.errors.stream(), Stream.of(parseError))
                        .sorted((a,b) -> b.location() - a.location())
                        .toList().subList(0, Math.min(10, this.errors.size() + 1)));
    }
}
