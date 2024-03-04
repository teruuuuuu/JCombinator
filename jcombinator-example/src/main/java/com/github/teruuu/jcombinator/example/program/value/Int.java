package com.github.teruuu.jcombinator.example.program.value;

public record Int(int value) implements Value {

    public String toString() {
        return String.valueOf(value);
    }
}
