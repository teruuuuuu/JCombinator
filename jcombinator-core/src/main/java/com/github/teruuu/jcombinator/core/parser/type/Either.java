package com.github.teruuu.jcombinator.core.parser.type;

public sealed interface Either<X, Y> {
    record Left<X, Y>(X value) implements Either<X, Y> {
    }

    record Right<X, Y>(Y value) implements Either<X, Y> {
    }

    static <X, Y> Either.Left<X, Y> left(X value) {
        return new Either.Left<>(value);
    }

    static <X, Y> Either.Right<X, Y> right(Y value) {
        return new Either.Right<>(value);
    }
}
