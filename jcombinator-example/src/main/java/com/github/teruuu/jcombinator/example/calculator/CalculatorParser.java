package com.github.teruuu.jcombinator.example.calculator;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.ParserBase;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * expr := term | expr '+' term | expr '-' term
 * term := factor | term '*' factor | term / factor
 * factor := '(' expr ')' | number
 * number := [0 - 9]*
 */
public class CalculatorParser implements Parser<Calculator> {
    public final Parser<Calculator> parser;

    public CalculatorParser() {
        this.parser = new Parser<>() {

            private final Parser<Calculator> numberParser = new ParserBase<>() {
                @Override
                protected Parser<Calculator> genParser() {
                    Parser<Calculator> parser = Parser.integer().map(Calculator.Number::new);
                    return parser.withSkipSpace();
                }
            };

            private final Parser<Calculator> factorParser = new ParserBase<>() {
                @Override
                protected Parser<Calculator> genParser() {
                    Parser<Calculator> bracketParser = (Parser.skip('(').withSkipSpace()).
                            andRight(exprParser.withSkipSpace()).
                            andLeft(Parser.skip(')').withSkipSpace()).map(Calculator.Bracket::new);
                    return bracketParser.or(numberParser);
                }
            };

            private final Parser<Calculator> termParser = new ParserBase<>() {
                @Override
                protected Parser<Calculator> genParser() {
                    Parser<Function<Calculator, Calculator>> mulGenParser = Parser.skip('*').withSkipSpace().and(factorParser).map(value ->
                            v -> new Calculator.Mul(v, value._2()));
                    Parser<Function<Calculator, Calculator>> divGenParser = Parser.skip('/').withSkipSpace().and(factorParser).map(value ->
                            v -> new Calculator.Div(v, value._2()));
                    return factorParser.and(mulGenParser.or(divGenParser).seq0()).map(value ->
                            foldLeft(value._1(), value._2(), (a, b) -> b.apply(a)));
                }
            };

            private final Parser<Calculator> exprParser = new ParserBase<>() {
                @Override
                protected Parser<Calculator> genParser() {
                    Parser<Function<Calculator, Calculator>> addGenParser = Parser.skip('+').withSkipSpace().and(termParser).map(value ->
                            v -> new Calculator.Add(v, value._2()));
                    Parser<Function<Calculator, Calculator>> subGenParser = Parser.skip('-').withSkipSpace().and(termParser).map(value ->
                            (v) -> new Calculator.Sub(v, value._2()));

                    return termParser.and(addGenParser.or(subGenParser).seq0()).map(value ->
                            foldLeft(value._1(), value._2(), (a, b) -> b.apply(a)));
                }
            };

            private <X, Y> X foldLeft(X x, List<Y> list, BiFunction<X, Y, X> func) {
                for (Y y : list) {
                    x = func.apply(x, y);
                }
                return x;
            }

            @Override
            public ParseResult<Calculator> parse(String input, int location) {
                return exprParser.parse(input, location);
            }
        };
    }

    @Override
    public ParseResult<Calculator> parse(String input, int location) {
        return parser.parse(input, location);
    }
}
