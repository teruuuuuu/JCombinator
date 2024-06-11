package com.github.teruuu.jcombinator.core.parser;

import java.util.function.Function;

public sealed interface ParseResult<T> {
    record Success<T>(T value, int location) implements ParseResult<T> {
        @Override
        public <U> ParseResult<U> map(Function<T, U> fn) {
            return new Success<>(fn.apply(value), location);
        }

        @Override
        public String toString() {
            return "Success(value[" + value + "], location[" + location + "])";
        }
    }

    record Failure<T>(ParseError parseError, int location) implements ParseResult<T> {

        @Override
        public <U> ParseResult<U> map(Function<T, U> fn) {
            return new Failure<>(this.parseError, this.location);
        }

        @Override
        public String toString() {
            return "Failure(" + this.parseError + ")";
        }
    }

    <U> ParseResult<U> map(Function<T, U> fn);
}
