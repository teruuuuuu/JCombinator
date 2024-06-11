package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Either;
import com.github.teruuu.jcombinator.core.parser.type.Tuple;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * パーサー用インターフェース
 *
 * @param <T> パース結果の型
 */
public interface Parser<T> {

    ParseResult<T> parse(String input, int location);

    default ParseResult<T> parse(String input) {
        return parse(input, 0);
    }

    default <X> Parser<Tuple<T, X>> and(Parser<X> parser) {
        return new AndParser<>(this, parser);
    }

    default <X> Parser<T> andLeft(Parser<X> parser) {
        return this.and(parser).map(Tuple::_1);
    }

    default <X> Parser<X> andRight(Parser<X> parser) {
        return this.and(parser).map(Tuple::_2);
    }

    default <X> Parser<Void> andVoid(Parser<X> parser) {
        return this.and(parser).map(value -> null);
    }

    static <T, X, Y, Z> Parser<List<T>> array(Parser<X> leftBracket, Parser<Y> rightBracket, Parser<Z> separator, Parser<T> parser) {
        return new ArrayParser<>(leftBracket, rightBracket, separator, parser);
    }

    default <X, Y, Z> Parser<List<T>> array(Parser<X> leftBracket, Parser<Y> rightBracket, Parser<Z> separator) {
        return new ArrayParser<>(leftBracket, rightBracket, separator, this);
    }

    default <X, Y> Parser<T> between(Parser<X> leftParser, Parser<Y> rightParser) {
        return leftParser.andRight(this).andLeft(rightParser);
    }

    static Parser<String> dQuoteString() {
        return new DQuoteStringParser();
    }

    static <X, Y> Parser<Either<X, Y>> either(Parser<X> firstParser, Parser<Y> secondParser) {
        return new EitherParser<>(firstParser, secondParser);
    }

    default <X> Parser<Either<T, X>> either(Parser<X> parser) {
        return new EitherParser<>(this, parser);
    }

    static Parser<Void> end() {
        return new EndParser();
    }

    default <X> Parser<X> flatMap(Function<T, Parser<X>> flatMapFunc) {
        Parser<T> capture = this;
        return (input, location) -> {
            switch (capture.parse(input, location)) {
                case ParseResult.Success<T> success -> {
                    return flatMapFunc.apply(success.value()).parse(input, success.location());
                }
                case ParseResult.Failure<T> failure -> {
                    return new ParseResult.Failure<>(failure.parseError(), failure.location());
                }
            }
        };
    }

    static Parser<String> escape() {
        return new EscapeParser();
    }

    static Parser<Integer> integer() {
        return new IntegerParser();
    }
    static Parser<String> literal(String literal) {
        return new StringParser(literal);
    }

    static Parser<String> literal(char c) {
        return new CharParser(c);
    }

    default <X> Parser<X> map(Function<T, X> mapFunc) {
        return (input, location) -> {
            switch (this.parse(input, location)) {
                case ParseResult.Success<T> success -> {
                    return new ParseResult.Success<>(mapFunc.apply(success.value()), success.location());
                }
                case ParseResult.Failure<T> failure -> {
                    return new ParseResult.Failure<>(failure.parseError(), failure.location());
                }
            }
        };
    }

    static <X> Parser<String> not(Parser<X> parser) {
        return new NotParser<>(parser);
    }

    default Parser<String> not() {
        return new NotParser<>(this);
    }

    static Parser<Either<Integer, Double>> number() {
        return new NumberParser();
    }

    static <X> Parser<Optional<X>> optional(Parser<X> parser) {
        return new OptionalParser<>(parser);
    }

    default Parser<Optional<T>> optional() {
        return new OptionalParser<>(this);
    }

    default Parser<T> or(Parser<T> parser) {
        return new OrParser<>(this, parser);
    }

    default <X> Parser<X> pure(X value) {
        return this.map(ignore -> value);
    }


    static Parser<String> range(char c1, char c2) {
        return new RangeParser(c1, c2);
    }

    default Parser<List<T>> seq0() {
        return new Seq0Parser<>(this);
    }

    default Parser<List<T>> seq1() {
        return new Seq1Parser<>(this);
    }

    static Parser<Void> skip(String literal) {
        return new SkipParser(literal);
    }

    static Parser<Void> skip(char c) {
        return new SkipCharParser(c);
    }

    static Parser<String> stopWord(String literal) {
        return new StopWardParser(literal);
    }

    static Parser<Void> space() {
        return new SpaceParser();
    }

    default Parser<T> withSkipSpace() {
        return new SkipSpaceParser().andRight(this);
    }
}