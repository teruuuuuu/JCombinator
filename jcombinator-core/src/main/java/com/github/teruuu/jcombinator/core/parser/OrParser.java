package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

import java.util.List;

public class OrParser<X> implements Parser<X> {
    private final Parser<X> firstParser;
    private final Parser<X> secondParser;

    public OrParser(Parser<X> firstParser, Parser<X> secondParser) {
        this.firstParser = firstParser;
        this.secondParser = secondParser;
    }

    @Override
    public Tuple<ParseContext, ParseResult<X>> parse(String input, ParseContext context) {
        Tuple<ParseContext, ParseResult<X>> fParseResultState = firstParser.parse(input, context);
        ParseContext fContext = fParseResultState._1();
        ParseResult<X> fParseResult = fParseResultState._2();
        switch (fParseResult) {
            case ParseResult.Success<X> fsuccess -> {
                return new Tuple<>(fContext, new ParseResult.Success<>(fsuccess.value()));
            }
            case ParseResult.Failure<X> ffailure -> {
                Tuple<ParseContext, ParseResult<X>> sParseResultState = secondParser.parse(input, context);
                ParseContext sContext = sParseResultState._1();
                ParseResult<X> sParseResult = sParseResultState._2();

                switch (sParseResult) {
                    case ParseResult.Success<X> ssuccess -> {
                        return new Tuple<>(sContext, new ParseResult.Success<>(ssuccess.value()));
                    }
                    case ParseResult.Failure<X> sfailure -> {
                        return new Tuple<>(
                                context.newError("or", "no valid parser").addError(fContext).addError(sContext),
                                new ParseResult.Failure<>());
                    }
                }
            }
        }
    }
}
