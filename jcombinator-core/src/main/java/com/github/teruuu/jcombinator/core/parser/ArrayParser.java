package com.github.teruuu.jcombinator.core.parser;

import java.util.List;
import java.util.stream.Stream;

public class ArrayParser<T, X, Y, Z> implements Parser<List<T>> {

    private final Parser<List<T>> parser;

    public ArrayParser(Parser<X> leftBracket, Parser<Y> rightBracket, Parser<Z> separator, Parser<T> parser) {
        this.parser = leftBracket.andRight(
                parser.and(separator.andRight(parser).seq0()).optional()
                        .map(value -> value.map(v -> Stream.concat(Stream.of(v._1()), v._2().stream()).toList()).orElse(List.of()))
        ).andLeft(rightBracket);
    }

    @Override
    public ParseResult<List<T>> parse(String input, int location) {
        return this.parser.parse(input, location);
    }
}
