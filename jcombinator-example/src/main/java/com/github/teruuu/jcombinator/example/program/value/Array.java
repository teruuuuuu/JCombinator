package com.github.teruuu.jcombinator.example.program.value;

import java.util.List;
import java.util.stream.Collectors;

public record Array(List<? extends Value> values) implements Value {

    public String toString() {
        return values.stream().map(Value::toString).collect(Collectors.joining(",", "[", "]"));
    }
}
