package com.github.teruuu.jcombinator.regexp.vm;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.ParserBase;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 *
 * expression := select
 * select := connect '|' connect | connect
 * connect := seq | connect seq
 * seq := primary '*' | primary '+' | primary '?' | primary seq_range | primary
 * seq_range := '{' [0-9]+ '}' | '{' [0-9]+  ',' [0-9]+ '}' | '{' ',' [0-9]+ '}' | '{' [0-9]+  ',' '}'
 * primary := '(' expression ')' | any | head | end | which | char
 * any := .
 * head := ^
 * end := $
 * char := [0-9a-zA-Z]
 * which := '[' '^'? which_inner_connect '-'? ']'
 * which_inner_connect := which_inner+
 * which_inner := char '-' char | char
 * look := look_forward | look_behind
 * look_forward := positive_look_forward | negative_look_forward
 * positive_look_forward := '(' '?' '=' expression ')'
 * negative_look_forward := '(' '?' '!' expression ')'
 * look_behind := positive_look_behind | negative_look_behind
 * positive_look_behind := '(' '?' '<' '=' expression ')'
 * negative_look_behind := '(' '?' '<' '!' expression ')'
 */
public class RegExpParser implements Parser<Rule> {
    private static final char ESCAPE = '\\';
    private static final char LEFT_BRACKET = '(';
    private static final char RIGHT_BRACKET = ')';

    private static final char LEFT_CURLY_BRACKET = '{';
    private static final char RIGHT_CURLY_BRACKET = '}';
    private static final char LEFT_SQUARE_BRACKET = '[';
    private static final char RIGHT_SQUARE_BRACKET = ']';
    private static final char PIPE = '|';
    private static final char ASTERISK = '*';

    private static final char DOT = '.';
    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final char QUESTION = '?';

    private static final char HAT = '^';
    private static final char DOL = '$';

    private static final char[] ESCAPE_WORDS = new char[]{
            ESCAPE, LEFT_BRACKET, RIGHT_BRACKET, LEFT_CURLY_BRACKET, RIGHT_CURLY_BRACKET,
            LEFT_SQUARE_BRACKET, RIGHT_SQUARE_BRACKET, PIPE, ASTERISK, DOT, PLUS,
            HAT, DOL
    };


    Parser<Rule> charParser = (input, location) -> {
        if (input.length() > location) {
            char c0 = input.charAt(location);
            if (c0 == ESCAPE) {
                if (input.length() > location + 1) {
                    char c1 = input.charAt(location);
                    for (char reservedWord : ESCAPE_WORDS) {
                        if (c1 == reservedWord) {
                            return new ParseResult.Success<>(Rule.literal(reservedWord), location + 2);
                        }
                    }
                    return new ParseResult.Failure<>("", location);
                }
            }
            for (char reservedWord : ESCAPE_WORDS) {
                if (c0 == reservedWord) {
                    return new ParseResult.Failure<>("", location);
                }
            }
            return new ParseResult.Success<>(Rule.literal(input.charAt(location)), location + 1);
        } else {
            return new ParseResult.Failure<>("", location);
        }
    };

    Parser<Rule> anyParser = (input, location) -> {
        if (input.length() > location && input.charAt(location) == DOT) {
            return new ParseResult.Success<>(Rule.any(), location + 1);
        } else {
            return new ParseResult.Failure<>("", location);
        }
    };

    Parser<Rule> headParser = (input, location) -> {
        if (input.length() > location && location == 0 && input.charAt(location) == HAT) {
            return new ParseResult.Success<>(Rule.head(), location + 1);
        } else {
            return new ParseResult.Failure<>("", location);
        }
    };

    Parser<Rule> endParser = (input, location) -> {
        if (input.length() - 1 == location && input.charAt(location) == DOL) {
            return new ParseResult.Success<>(Rule.end(), location + 1);
        } else {
            return new ParseResult.Failure<>("", location);
        }
    };

    // ex: [a-z]
    Parser<Rule> whichParser = new ParserBase<>() {

        Parser<Integer> charParser = (input, location) -> {
            if (input.length() > location) {
                char c0 = input.charAt(location);
                if (c0 == ESCAPE) {
                    location += 1;
                    boolean escapeOk = false;
                    if (input.length() > location + 1) {
                        c0 = input.charAt(location);
                        for (char reservedWord : ESCAPE_WORDS) {
                            if (c0 == reservedWord) {
                                escapeOk = true;
                                break;
                            }
                        }
                    }
                    if (!escapeOk) {
                        return new ParseResult.Failure<>("", location);
                    }
                } else {
                    for (char reservedWord : ESCAPE_WORDS) {
                        if (c0 == reservedWord) {
                            return new ParseResult.Failure<>("", location);
                        }
                    }
                }
                return new ParseResult.Success<>((int)c0, location + 1);
            } else {
                return new ParseResult.Failure<>("", location);
            }
        };
        final Parser<Rule> whichInner = charParser.andLeft(Parser.literal(MINUS)).and(charParser).
                map(chars -> Rule.range((char)chars._1().intValue(), (char) chars._2().intValue())).or(charParser.map(c -> Rule.literal((char)c.intValue())));

        @Override
        protected Parser<Rule> genParser() {
            return Parser.literal(LEFT_SQUARE_BRACKET).flatMap(leftBracket ->
                    Parser.literal(HAT).optional().flatMap(hatOpt ->
                            whichInner.and(whichInner.seq0()).flatMap(inner ->
                                            Parser.literal(RIGHT_SQUARE_BRACKET).map(rightBracket ->
                                                    hatOpt.isEmpty() ?
                                                            foldLeft(inner._1(), inner._2(), Rule::select)
                                                            : Rule.not(foldLeft(inner._1(), inner._2(), Rule::select))
                                            )
                            )
                    )
            );
        }
    };

    Parser<Rule> lookParser = new ParserBase<>() {

        @Override
        protected Parser<Rule> genParser() {
            return Parser.literal(LEFT_BRACKET).and(Parser.literal('?')).flatMap(left ->
                    Parser.literal('=').flatMap(literal ->
                            expressionParser.map(Rule::lookAhead)
                    ).or(
                            Parser.literal('!').flatMap(literal ->
                                    expressionParser.map(Rule::notLookAhead)
                            )
                    ).or(
                            Parser.literal('<').flatMap(literal1 ->
                                    Parser.literal('=').flatMap(literal2 ->
                                            expressionParser.map(Rule::lookBehind)
                                    )
                            )
                    ).or(
                            Parser.literal('<').flatMap(literal1 ->
                                    Parser.literal('=').flatMap(literal2 ->
                                            expressionParser.map(Rule::notLookBehind)
                                    )
                            )

                    ).flatMap(rule ->
                            Parser.literal(')').map(right ->
                                    rule
                            )
                    )
            );
        }
    };



    Parser<Rule> parser = new ParserBase<>() {

        @Override
        protected Parser<Rule> genParser() {
            return expressionParser;
        }
    };

    Parser<Rule> expressionParser = new ParserBase<>() {

        @Override
        protected Parser<Rule> genParser() {
            return selectParser;
        }
    };

    Parser<Rule> selectParser = new ParserBase<>() {
        @Override
        protected Parser<Rule> genParser() {
            return connectParser.and(Parser.literal(PIPE).andRight(connectParser).seq0()).map(rules ->
                    foldLeft(rules._1(), rules._2(), Rule::select)
            );
        }
    };

    Parser<Rule> connectParser = new ParserBase<>() {
        @Override
        protected Parser<Rule> genParser() {
            return seqParser.and(seqParser.seq0()).map(rules ->
                    foldLeft(rules._1(), rules._2(), Rule::cons)
            );
        }
    };

    // * + ?
    Parser<Rule> seqParser = new ParserBase<>() {
        @Override
        protected Parser<Rule> genParser() {
            return primaryParser.flatMap(rule ->
                    Parser.literal(ASTERISK).map(a -> Rule.zeroSeq(rule)).
                            or(Parser.literal(PLUS).map(a -> Rule.oneSeq(rule))).
                            or(Parser.literal(QUESTION).map(a -> Rule.option(rule))).
                            or(seqRangeParser.map(seqRange -> seqRange.apply(rule)).
                            or((input, location) -> new ParseResult.Success<>(rule, location))));

        }
    };

    Parser<Function<Rule,Rule>> seqRangeParser = new ParserBase<>() {
        final Parser<Integer> numberParser = (input, location) -> {
            int ret = 0;
            boolean isNumber = false;
            while(true) {
                if (input.length() == location || input.charAt(location) > '9' || input.charAt(location) < '0') {
                    break;
                } else {
                    ret *= 10;
                    ret += input.charAt(location) - '0';
                    location += 1;
                    isNumber = true;
                }

            }
            if (isNumber) {
                return new ParseResult.Success<>(ret, location);
            } else {
                return new ParseResult.Failure<>("", location);
            }
        };

        final Parser<Function<Rule, Rule>> quantityParser =
                numberParser.flatMap(num1 ->
                        Parser.literal(',').flatMap(comma ->
                                numberParser.map(num2 ->
                                        (Function<Rule, Rule>)rule -> Rule.quantityMoreLess(rule, num1, num2)
                        )
                )).or(
                        numberParser.flatMap(num1 ->
                                        Parser.literal(',').map(comma ->
                                                rule -> Rule.quantityMore(rule, num1)
                                        )
                )).or(
                        Parser.literal(',').flatMap(comma ->
                                numberParser.map(num2 ->
                                        rule -> Rule.quantityLess(rule, num2)
                                )
                )).or(
                        numberParser.map(num ->
                                rule -> Rule.quantity(rule, num)
                        )
                );

        @Override
        protected Parser<Function<Rule, Rule>> genParser() {
            return Parser.literal(LEFT_CURLY_BRACKET).flatMap(leftBracket ->
                            quantityParser.flatMap(quantity ->
                                    Parser.literal(RIGHT_CURLY_BRACKET).map(rightBracket ->
                                            quantity)
                            )
            );
        }
    };

    Parser<Rule> primaryParser = new ParserBase<>() {
        @Override
        protected Parser<Rule> genParser() {
            return headParser
                    .or(endParser)
                    .or(anyParser)
                    .or(whichParser)
                    .or(lookParser)
                    .or(
                            Parser.literal(LEFT_BRACKET).flatMap(leftBracket ->
                                    expressionParser.flatMap(rule ->
                                            Parser.literal(RIGHT_BRACKET).
                                                    pure(rule)
                                    )
                            )
                    )
                    .or(charParser);
        }
    };

    private <X, Y> X foldLeft(X x, List<Y> list, BiFunction<X, Y, X> func) {
        for (Y y : list) {
            x = func.apply(x, y);
        }
        return x;
    }


    @Override
    public ParseResult<Rule> parse(String input, int location) {
        return parser.parse(input, location);
    }
}
