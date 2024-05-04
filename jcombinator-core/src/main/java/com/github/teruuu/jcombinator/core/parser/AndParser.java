package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class AndParser<X, Y> implements Parser<Tuple<X, Y>> {

    private final Parser<X> firstParser;
    private final Parser<Y> secondParser;

    public AndParser(Parser<X> firstParser, Parser<Y> secondParser) {
        this.firstParser = firstParser;
        this.secondParser = secondParser;
    }

    @Override
    public Tuple<ParseContext, ParseResult<Tuple<X, Y>>> parse(String input, ParseContext context) {
        Tuple<ParseContext, ParseResult<X>> fParseResultState = firstParser.parse(input, context);
        ParseContext fContext = fParseResultState._1();
        ParseResult<X> fParseResult = fParseResultState._2();
        switch (fParseResult) {
            case ParseResult.Success<X> fsuccess -> {
                Tuple<ParseContext, ParseResult<Y>> sParseResultState = secondParser.parse(input, context.newLocation(fContext.location()));
                ParseContext sContext = sParseResultState._1();
                ParseResult<Y> sParseResult = sParseResultState._2();

                switch (sParseResult) {
                    case ParseResult.Success<Y> ssuccess -> {
                        return new Tuple<>(sContext,
                                new ParseResult.Success<>(new Tuple<>(fsuccess.value(), ssuccess.value())));
                    }
                    case ParseResult.Failure<Y> sfailure -> {
                        return new Tuple<>(
                                context.newError("and", "and parse error").addError(fContext).addError(sContext),
                                new ParseResult.Failure<>());
                    }
                }
            }
            case ParseResult.Failure<X> ffailure -> {
                return new Tuple<>(fContext, new ParseResult.Failure<>());
            }
        }
    }
}
