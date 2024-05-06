package com.github.teruuu.jcombinator.core.parser;

public record ParseContext(String label, int location, ParseError parseError) {

    public static ParseContext context(int location) {
        return new ParseContext(null, location, null);
    }
    public static ParseContext context(String label, int location) {
        return new ParseContext(label, location, null);
    }

    public ParseContext newLocation(int location) {
        return new ParseContext(this. label, location, this.parseError);
    }

    public ParseContext move(int move) {
        return new ParseContext(this.label, this.location + move, this.parseError);
    }

    public ParseContext newError(int location, String label, String message) {
        return new ParseContext(this.label, location, ParseError.newError(location, label, message));
    }

    public ParseContext newError(String label, String message) {
        return new ParseContext(this.label, this.location, ParseError.newError(this.location, label, message));
    }

    public ParseContext addError(ParseContext context) {
        return addError(context.parseError());
    }

    public ParseContext addError(ParseError parseError) {
        if (this.parseError() != null && parseError != null) {
            if (this.label == null || parseError.label().equals(this.label)) {
                return new ParseContext(
                        this.label,
                        Math.max(this.location(), parseError.location()),
                        this.parseError().addError(parseError)
                );
            } else {
                ParseContext ret = new ParseContext(
                        this.label,
                        Math.max(this.location(), parseError.location()),
                        this.parseError
                );
                for (ParseError error: parseError.children()) {
                    ret = ret.addError(error);
                }
                return ret;
            }
        } else {
            return new ParseContext(
                    this.label,
                    this.location(),
                    this.parseError
            );
        }
    }
}
