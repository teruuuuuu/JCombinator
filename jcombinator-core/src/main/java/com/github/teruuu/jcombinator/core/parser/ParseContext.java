package com.github.teruuu.jcombinator.core.parser;

public record ParseContext(int location, ParseError parseError) {

    public static ParseContext context(int location) {
        return new ParseContext(location, null);
    }

    public ParseContext newLocation(int location) {
        return new ParseContext(location, this.parseError);
    }

    public ParseContext move(int move) {
        return new ParseContext(this.location + move, this.parseError);
    }

    public ParseContext newError(int location, String label, String message) {
        return new ParseContext(location, ParseError.newError(location, label, message));
    }

    public ParseContext newError(String label, String message) {
        return new ParseContext(this.location, ParseError.newError(this.location, label, message));
    }

    public ParseContext addError(ParseContext context) {
        if (context.parseError() != null && this.parseError() != null) {
            return new ParseContext(Math.max(this.location(), context.parseError().location()),
                    this.parseError().addError(context.parseError()));
        } else {
            return new ParseContext(this.location(), this.parseError);
        }
    }
}
