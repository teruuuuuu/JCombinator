package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

import java.util.List;

public class AndParser<X, Y> implements Parser<Tuple<X, Y>> {

    private final Parser<X> firstParser;
    private final Parser<Y> secondParser;

    public AndParser(Parser<X> firstParser, Parser<Y> secondParser) {
        this.firstParser = firstParser;
        this.secondParser = secondParser;
    }

    @Override
    public ParseResult<Tuple<X, Y>> parse(String input, int location) {
        switch (firstParser.parse(input, location)) {
            case ParseResult.Success<X> fsuccess -> {
                switch (secondParser.parse(input, fsuccess.location())) {
                    case ParseResult.Success<Y> ssuccess -> {
                        return new ParseResult.Success<>(new Tuple<>(fsuccess.value(), ssuccess.value()), ssuccess.location());
                    }
                    case ParseResult.Failure<Y> sfailure -> {
                        return new ParseResult.Failure<>(
                                new ParseError("and", "", location, List.of(sfailure.parseError())),
                                location);
                    }
                }
            }
            case ParseResult.Failure<X> ffailure -> {
                return new ParseResult.Failure<>(
                        new ParseError("and", "", location, List.of(ffailure.parseError())),
                        location);
            }
        }
    }
}
