package com.github.teruuu.jcombinator.core.parser;

import java.util.Comparator;
import java.util.List;

public record ParseError(String label, String message, int location, List<ParseError> children) {

    public int lastLocation() {
        if (children.isEmpty()) {
            return location;
        } else {
            return Math.max(location, children.stream().map(ParseError::lastLocation).max(Comparator.naturalOrder()).orElse(0));
        }
    }
}
