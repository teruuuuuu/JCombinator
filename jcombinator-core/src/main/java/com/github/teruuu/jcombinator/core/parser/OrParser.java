package com.github.teruuu.jcombinator.core.parser;

import java.util.List;

public class OrParser<X> implements Parser<X> {
    private final Parser<X> firstParser;
    private final Parser<X> secondParser;

    public OrParser(Parser<X> firstParser, Parser<X> secondParser) {
        this.firstParser = firstParser;
        this.secondParser = secondParser;
    }

    @Override
    public ParseResult<X> parse(String input, int location) {
        switch (firstParser.parse(input, location)) {
            case ParseResult.Success<X> fsuccess -> {
                return fsuccess;
            }
            case ParseResult.Failure<X> ffailure -> {
                switch (secondParser.parse(input, location)) {
                    case ParseResult.Success<X> ssuccess -> {
                        return ssuccess;
                    }
                    case ParseResult.Failure<X> sfailure -> {
                        return new ParseResult.Failure<>(String.join(",", List.of(ffailure.message(), sfailure.message())), location);
                    }
                }
            }
        }
    }
}
