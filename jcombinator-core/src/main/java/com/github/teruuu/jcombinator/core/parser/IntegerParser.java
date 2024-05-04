package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class IntegerParser implements Parser<Integer> {
    int reallyBig = Integer.MAX_VALUE / 10;

    @Override
    public Tuple<ParseContext, ParseResult<Integer>> parse(String input, ParseContext context) {
        int location = context.location();
        int signe = 1;
        int number = 0;
        int length = input.length();
        if (length <= location) {
            return new Tuple<>(context.newError(location, "integer", "index out of bounds"), new ParseResult.Failure<>());
        }

        if (input.charAt(location) == '-') {
            signe = -1;
            location++;
        } else if (input.charAt(location) == '+') {
            signe = 1;
            location++;
        }

        if (input.charAt(location) >= '0' && input.charAt(location) <= '9') {
            // 整数部分
            while (length > location && input.charAt(location) >= '0' && input.charAt(location) <= '9') {
                if (number >= reallyBig) {
                    return new Tuple<>(
                            context.newError(
                                    location,
                                    "integer",
                                    String.format("exceeds the limit(location=[%d] input=[%s]", location, input)
                            ),
                            new ParseResult.Failure<>());
                }
                number *= 10;
                number += (input.charAt(location) - '0');
                location++;
            }
            return new Tuple<>(context.newLocation(location), new ParseResult.Success<>(signe * number));
        } else {
            return new Tuple<>(
                    context.newError(
                            location,
                            "integer",
                            String.format("not number(location=[%d] input=[%s]", location, input)
                    ),
                    new ParseResult.Failure<>());
        }
    }
}
