package com.github.teruuu.jcombinator.example.program.value;

public record Str(String value) implements Value {

    public String toString() {
        return value;
    }
}
