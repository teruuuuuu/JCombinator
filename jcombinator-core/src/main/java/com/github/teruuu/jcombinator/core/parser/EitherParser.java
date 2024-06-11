package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Either;

import java.util.List;

public class EitherParser<X, Y> implements Parser<Either<X, Y>> {

    private final Parser<X> firstParser;
    private final Parser<Y> secondParser;

    public EitherParser(Parser<X> firstParser, Parser<Y> secondParser) {
        this.firstParser = firstParser;
        this.secondParser = secondParser;
    }

    @Override
    public ParseResult<Either<X, Y>> parse(String input, int location) {
        switch (firstParser.parse(input, location)) {
            case ParseResult.Success<X> fsuccess -> {
                return new ParseResult.Success<>(new Either.Left<>(fsuccess.value()), fsuccess.location());
            }
            case ParseResult.Failure<X> ffailure -> {
                switch (secondParser.parse(input, location)) {
                    case ParseResult.Success<Y> ssuccess -> {
                        return new ParseResult.Success<>(new Either.Right<>(ssuccess.value()), ssuccess.location());
                    }
                    case ParseResult.Failure<Y> sfailure -> {
                        return new ParseResult.Failure<>(
                                new ParseError(
                                        "either", "",
                                        Math.max(ffailure.parseError().location(), sfailure.parseError().location()),
                                        List.of(ffailure.parseError(), sfailure.parseError())
                                ),
                                location
                        );
                    }
                }
            }
        }
    }
}
