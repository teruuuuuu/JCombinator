package com.github.teruuu.jcombinator.core.parser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public record ParseError(int location, String label, String message, List<ParseError> children) {

    public static ParseError newError(int location, String label, String message) {
        return new ParseError(location, label, message, List.of());
    }

    public static ParseError newError(int location, String label, String message, List<ParseError> errors) {
        return new ParseError(
                errors.stream().map(e -> e.location).max(Comparator.reverseOrder()).orElse(location),
                label,
                message,
                errors
        );
    }

    public ParseError addError(ParseError error) {
        return new ParseError(
                Math.max(error.location, this.location),
                label,
                message,
                Stream.concat(children.stream(), Stream.of(error)).toList());
    }

    public int count() {
        return children.stream().map(ParseError::count).reduce(Integer::sum).orElse(0) + 1;
    }

    public Stream<ParseError> stream() {
        return Stream.concat(Stream.of(newError(this.location, this.label, this.message, List.of())), children().stream().flatMap(ParseError::stream));
    }

    public List<ParseError> labelFilter(String label) {
        if (this.label.equals(label)) {
            return Stream.concat(Stream.of(newError(this.location, this.label, this.message)),
                    children.stream().flatMap(e -> e.labelFilter(label).stream())).distinct().toList();
        } else {
            return children.stream().flatMap(e -> e.labelFilter(label).stream()).distinct().toList();
        }
    }
}
