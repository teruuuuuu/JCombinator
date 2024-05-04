package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Either;
import com.github.teruuu.jcombinator.core.parser.type.Tuple;

import java.util.List;

public class EitherParser<X, Y> implements Parser<Either<X, Y>> {

    private final Parser<X> firstParser;
    private final Parser<Y> secondParser;

    public EitherParser(Parser<X> firstParser, Parser<Y> secondParser) {
        this.firstParser = firstParser;
        this.secondParser = secondParser;
    }

    @Override
    public Tuple<ParseContext, ParseResult<Either<X, Y>>> parse(String input, ParseContext context) {
        Tuple<ParseContext, ParseResult<X>> fParseResultState = firstParser.parse(input, context);
        ParseContext fContext = fParseResultState._1();
        ParseResult<X> fParseResult = fParseResultState._2();
        switch (fParseResult) {
            case ParseResult.Success<X> fsuccess -> {
                return new Tuple<>(
                        context.newLocation(fContext.location()).addError(fContext),
                        new ParseResult.Success<>(new Either.Left<>(fsuccess.value())));
            }
            case ParseResult.Failure<X> ffailure -> {
                Tuple<ParseContext, ParseResult<Y>> sParseResultState = secondParser.parse(input, context);
                ParseContext sContext = sParseResultState._1();
                ParseResult<Y> sParseResult = sParseResultState._2();
                switch (sParseResult) {
                    case ParseResult.Success<Y> ssuccess -> {
                        return new Tuple<>(
                                sContext,
                                new ParseResult.Success<>(new Either.Right<>(ssuccess.value()))
                        );
                    }
                    case ParseResult.Failure<Y> sfailure -> {
                        return new Tuple<>(
                                context.newError("either", "no valid parser").addError(fContext).addError(sContext),
                                new ParseResult.Failure<>()
                        );
                    }
                }
            }
        }
    }
}
