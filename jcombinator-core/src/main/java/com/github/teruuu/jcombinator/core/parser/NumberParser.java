package com.github.teruuu.jcombinator.core.parser;

import com.github.teruuu.jcombinator.core.parser.type.Either;
import com.github.teruuu.jcombinator.core.parser.type.Tuple;

public class NumberParser implements Parser<Either<Integer, Double>> {
    int reallyBig = Integer.MAX_VALUE / 10;
    double reallyMini = Double.MAX_VALUE / 10;

    @Override
    public Tuple<ParseContext, ParseResult<Either<Integer, Double>>> parse(String input, ParseContext context) {
        int location = context.location();
        int signe = 1;
        int number = 0;
        int length = input.length();
        double decimal = 0;
        double decimalDigit = 1;
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
                            context.newError(location, "number", String.format("exceeds the limit(location=[%d] input=[%s]", location, input)),
                            new ParseResult.Failure<>()
                    );
                }
                number *= 10;
                number += (input.charAt(location) - '0');
                location++;
            }
        } else {
            return new Tuple<>(
                    context.newError(location, "number", String.format("not number(location=[%d] input=[%s]", location, input)),
                    new ParseResult.Failure<>()
            );
        }
        if (length == location || input.charAt(location) != '.') {
            return new Tuple<>(
                    context.newLocation(location),
                    new ParseResult.Success<>(new Either.Left<>(signe * number))
            );
        } else {
            location++;
            // 小数部分
            while (length > location && input.charAt(location) >= '0' && input.charAt(location) <= '9') {
                if (decimalDigit >= reallyMini) {
                    return new Tuple<>(
                            context.newError(location, "number", String.format("exceeds the limit(location=[%d] input=[%s]", location, input)),
                            new ParseResult.Failure<>()
                    );
                }
                decimal *= 10;
                decimal += (input.charAt(location) - '0');
                decimalDigit *= 10;
                location++;
            }
            decimal = decimal / decimalDigit;
            return new Tuple<>(
                    context.newLocation(location),
                    new ParseResult.Success<>(new Either.Right<>(signe * ((double) number + decimal)))
            );
        }
    }
}
