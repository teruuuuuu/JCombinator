package com.github.teruuu.jcombinator.example.program.value;

public record Bool(boolean value) implements Value {

    public String toString() {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
}
