package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Either;

public class EitherParser<X, Y> implements Parser<Either<X, Y>> {

    private final Parser<X> firstParser;
    private final Parser<Y> secondParser;

    public EitherParser(Parser<X> firstParser, Parser<Y> secondParser) {
        this.firstParser = firstParser;
        this.secondParser = secondParser;
    }

    @Override
    public ParseResult<Either<X, Y>> parse(String input, ParserContext context) {
        switch (firstParser.parse(input, context)) {
            case ParseResult.Success<X> fsuccess -> {
                return new ParseResult.Success<>(new Either.Left<>(fsuccess.value()), fsuccess.context());
            }
            case ParseResult.Failure<X> ffailure -> {
                switch (secondParser.parse(input, context)) {
                    case ParseResult.Success<Y> ssuccess -> {
                        return new ParseResult.Success<>(new Either.Right<>(ssuccess.value()), ssuccess.context());
                    }
                    case ParseResult.Failure<Y> sfailure -> {

                        return new ParseResult.Failure<>(
                                context.newError(ffailure.context().lastError()).newError(sfailure.context().lastError()));
                    }
                }
            }
        }
    }
}
