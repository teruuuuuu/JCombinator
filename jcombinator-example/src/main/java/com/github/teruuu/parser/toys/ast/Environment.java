package com.github.teruuu.parser.toys.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Environment(Map<String, Value> bindings, Optional<Environment> next) {
    public Optional<Map<String, Value>> findBinding(String name) {
        if (bindings.get(name) != null) return Optional.of(bindings);
        if (next.isPresent()) {
            return next.get().findBinding(name);
        } else {
            return Optional.empty();
        }
    }

    public static Environment newEnvironment(Optional<Environment> next) {
        return new Environment(new HashMap<>(), next);
    }
}
