package com.github.teruuu.jcombinator.core.parser;

public class IntegerParser implements Parser<Integer> {
    int reallyBig = Integer.MAX_VALUE / 10;

    @Override
    public ParseResult<Integer> parse(String input, ParserContext context) {
        int signe = 1;
        int number = 0;
        int length = input.length();
        int location = context.location();
        if (length <= location) {
            return new ParseResult.Failure<>(context.newError("index out of bounds"));
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
                    return new ParseResult.Failure<>(context.newError(location, String.format("exceeds the limit(location=[%d] input=[%s]", location, input)));
                }
                number *= 10;
                number += (input.charAt(location) - '0');
                location++;
            }
            return new ParseResult.Success<>(signe * number, context.nextLocation(location));
        } else {
            return new ParseResult.Failure<>(context.newError(location, String.format("not number(location=[%d] input=[%s]", location, input)));
        }
    }
}
