package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

import java.util.ArrayList;
import java.util.List;

public class Seq1Parser<T> implements Parser<List<T>> {
    private final Parser<T> parser;

    public Seq1Parser(Parser<T> parser) {
        this.parser = parser;
    }

    @Override
    public Tuple<ParseContext, ParseResult<List<T>>> parse(String input, ParseContext context) {
        int location = context.location();
        List<T> ret = new ArrayList<>();
        while (true) {
            Tuple<ParseContext, ParseResult<T>> fParseResultState = parser.parse(input, context.newLocation(location));
            ParseContext fContext = fParseResultState._1();
            ParseResult<T> fParseResult = fParseResultState._2();
            if (fParseResult instanceof ParseResult.Success<T> success) {
                ret.add(success.value());
                location = fContext.location();
            } else {
                context = context.newError("seq1", "parse seq stop").addError(fContext);
                break;
            }
        }
        if (!ret.isEmpty()) {
            return new Tuple<>(context, new ParseResult.Success<>(ret));
        } else {
            return new Tuple<>(context, new ParseResult.Failure<>());
        }
    }
}
