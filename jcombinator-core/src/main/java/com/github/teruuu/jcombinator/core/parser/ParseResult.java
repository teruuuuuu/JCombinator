package com.github.teruuu.jcombinator.core.parser;

import java.util.function.Function;

public sealed interface ParseResult<T> {
    record Success<T>(T value, int next) implements ParseResult<T> {
        @Override
        public <U> ParseResult<U> map(Function<T, U> fn) {
            return new Success<U>(fn.apply(value), next);
        }

        @Override
        public String toString() {
            return "Success(" + value + ", " + next + ")";
        }
    }

    record Failure<T>(String message, int next) implements ParseResult<T> {

        @Override
        public <U> ParseResult<U> map(Function<T, U> fn) {
            return new Failure<>(this.message, this.next);
        }

        @Override
        public String toString() {
            return "Failure(" + message + ", " + next;
        }
    }

    <U> ParseResult<U> map(Function<T, U> fn);
}
