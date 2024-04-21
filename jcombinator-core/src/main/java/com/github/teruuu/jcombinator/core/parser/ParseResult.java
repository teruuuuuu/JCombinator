package com.github.teruuu.jcombinator.core.parser;

import java.util.function.Function;

public sealed interface ParseResult<T> {
    record Success<T>(T value, ParserContext context) implements ParseResult<T> {
        @Override
        public <U> ParseResult<U> map(Function<T, U> fn) {
            return new Success<>(fn.apply(value), context);
        }

        @Override
        public String toString() {
            return "Success(" + value + ", " + context.location() + ")";
        }
    }

    record Failure<T>(ParserContext context) implements ParseResult<T> {

        @Override
        public <U> ParseResult<U> map(Function<T, U> fn) {
            return new Failure<>(this.context);
        }

        @Override
        public String toString() {
            return "Failure(" + context.lastError() + ")";
        }
    }

    <U> ParseResult<U> map(Function<T, U> fn);
}
