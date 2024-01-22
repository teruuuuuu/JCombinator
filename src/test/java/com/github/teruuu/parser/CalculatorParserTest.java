package com.github.teruuu.parser;

import com.github.teruuu.parser.common.ParseResult;
import com.github.teruuu.parser.common.Parser;
import com.github.teruuu.parser.common.ParserBase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalculatorParserTest {

    // expr := <term> | <expr> + <term> | <expr> - <term>
    // term := <factor> | <term> * <factor> | <term> / <factor>
    // factor := (<expr>) | <number>
    // number := [0 - 9]*

    sealed interface Calculator {
        record Bracket(Calculator value) implements Calculator {
            public Integer eval() {
                return value.eval();
            }
        }
        record Number(Integer value) implements Calculator {
            public Integer eval() {
                return value;
            }
        }
        record Mul(Calculator left, Calculator right) implements Calculator {
            public Integer eval() {
                return left.eval() * right.eval();
            }
        }
        record Div(Calculator left, Calculator right) implements Calculator {
            public Integer eval() {
                return left.eval() / right.eval();
            }
        }
        record Add(Calculator left, Calculator right) implements Calculator {
            public Integer eval() {
                return left.eval() + right.eval();
            }
        }
        record Sub(Calculator left, Calculator right) implements Calculator {
            public Integer eval() {
                return left.eval() - right.eval();
            }
        }

        Integer eval();
    }

    Parser<Calculator> parser = new Parser<>() {

        Parser<Calculator> numberParser = new ParserBase<>() {
            @Override
            protected Parser<Calculator> genParser() {
                Parser<Calculator> parser = Parser.integer().map(Calculator.Number::new);
                return parser.withSkipSpace();
            }
        };

        Parser<Calculator> factorParser = new ParserBase<>() {
            @Override
            protected Parser<Calculator> genParser() {
                Parser<Calculator> bracketParser = (Parser.skip('(').withSkipSpace()).
                        andRight(exprParser.withSkipSpace()).
                        andLeft(Parser.skip(')').withSkipSpace()).map(Calculator.Bracket::new);
                return bracketParser.or(numberParser);
            }
        };

        Parser<Calculator> termParser = new ParserBase<>() {
            @Override
            protected Parser<Calculator> genParser() {
                Parser<Function<Calculator,Calculator>> mulGenParser = Parser.skip('*').withSkipSpace().and(factorParser).map(value ->
                        v -> new Calculator.Mul(v, value._2()));
                Parser<Function<Calculator,Calculator>> divGenParser = Parser.skip('/').withSkipSpace().and(factorParser).map(value ->
                        v -> new Calculator.Div(v, value._2()));
                return factorParser.and(mulGenParser.or(divGenParser).seq()).map(value ->
                    foldLeft(value._1(), value._2(), (a,b) -> b.apply(a)));
            }
        };

        Parser<Calculator> exprParser = new ParserBase<>() {
            @Override
            protected Parser<Calculator> genParser() {
                Parser<Function<Calculator,Calculator>> addGenParser = Parser.skip('+').withSkipSpace().and(termParser).map(value ->
                        v -> new Calculator.Add(v, value._2()));
                Parser<Function<Calculator,Calculator>> subGenParser = Parser.skip('-').withSkipSpace().and(termParser).map(value ->
                        (v) -> new Calculator.Sub(v, value._2()));

                return termParser.and(addGenParser.or(subGenParser).seq()).map(value ->
                        foldLeft(value._1(), value._2(), (a,b) -> b.apply(a)));
            }
        };

        private <X,Y> X foldLeft(X x, List<Y> list, BiFunction<X,Y,X> func) {
            for (Y y: list) {
                x = func.apply(x, y);
            }
            return x;
        }

        @Override
        public ParseResult<Calculator> parse(String input, int location) {
            return exprParser.parse(input, location);
        }
    };


    @Test
    public void test() {
        ParseResult<Calculator> parseResult;
        Calculator value;

        parseResult = parser.parse("1234567890");
        assertTrue(parseResult instanceof ParseResult.Success<Calculator>);
        value = ((ParseResult.Success<Calculator>) parseResult).value();
        assertEquals(value, new Calculator.Number(1234567890));

        parseResult = parser.parse("(3456)");
        assertTrue(parseResult instanceof ParseResult.Success<Calculator>);
        value = ((ParseResult.Success<Calculator>) parseResult).value();
        assertEquals(value, new Calculator.Bracket(new Calculator.Number(3456)));

        parseResult = parser.parse("1 - 3 + 6 * 10 / 5");
        assertTrue(parseResult instanceof ParseResult.Success<Calculator>);
        value = ((ParseResult.Success<Calculator>) parseResult).value();
        assertEquals(value,
                new Calculator.Add(
                    new Calculator.Sub(
                            new Calculator.Number(1),
                            new Calculator.Number(3)
                    ),
                    new Calculator.Div(
                            new Calculator.Mul(
                                    new Calculator.Number(6),
                                    new Calculator.Number(10)
                            ),
                            new Calculator.Number(5)
                    )
                )
        );
        assertEquals(value.eval(), 10);

        parseResult = parser.parse("(1 + 3) * 5");
        assertTrue(parseResult instanceof ParseResult.Success<Calculator>);
        value = ((ParseResult.Success<Calculator>) parseResult).value();
        assertEquals(value,
                new Calculator.Mul(
                        new Calculator.Bracket(new Calculator.Add(
                                new Calculator.Number(1),
                                new Calculator.Number(3)
                        )),
                        new Calculator.Number(5)
                ));
        assertEquals(value.eval(), 20);
    }
}
