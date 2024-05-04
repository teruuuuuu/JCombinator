package com.github.teruuu.jcombinator.core.parser;

import java.util.function.Function;

public sealed interface ParseResult<T> {
    record Success<T>(T value) implements ParseResult<T> {
        @Override
        public <U> ParseResult<U> map(Function<T, U> fn) {
            return new Success<U>(fn.apply(value));
        }

        @Override
        public String toString() {
            return "Success(" + value + ")";
        }
    }

    record Failure<T>() implements ParseResult<T> {

        @Override
        public <U> ParseResult<U> map(Function<T, U> fn) {
            return new Failure<>();
        }

        @Override
        public String toString() {
            return "Failure()";
        }
    }

    <U> ParseResult<U> map(Function<T, U> fn);
}
