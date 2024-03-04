package com.github.teruuu.jcombinator.example.program.value;

public record Char(char v) implements Value {

    public String toString() {
        return String.valueOf(v);
    }
}
