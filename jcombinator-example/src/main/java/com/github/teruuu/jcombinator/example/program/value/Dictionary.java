package com.github.teruuu.jcombinator.example.program.value;

import java.util.Map;
import java.util.stream.Collectors;

public record Dictionary(Map<String, Value> v) implements Value {

    public String toString() {
        return v.keySet().stream().map(value -> String.format("%s->%s", value, value)).collect(Collectors.joining(",", "{", "}"));
    }
}
