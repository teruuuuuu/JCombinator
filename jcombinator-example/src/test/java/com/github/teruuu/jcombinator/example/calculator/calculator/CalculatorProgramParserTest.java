package com.github.teruuu.jcombinator.example.calculator.calculator;

import com.github.teruuu.jcombinator.core.parser.ParseContext;
import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.type.Tuple;
import com.github.teruuu.jcombinator.example.calculator.Calculator;
import com.github.teruuu.jcombinator.example.calculator.CalculatorParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalculatorProgramParserTest {

    @Test
    public void test() {
        Tuple<ParseContext, ParseResult<Calculator>> parseResultState;
        ParseResult<Calculator> parseResult;
        Calculator value;
        Parser<Calculator> parser = new CalculatorParser();

        parseResultState = parser.parse("1234567890");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Calculator>);
        value = ((ParseResult.Success<Calculator>) parseResult).value();
        assertEquals(value, new Calculator.Number(1234567890));

        parseResultState = parser.parse("(3456)");
        parseResult = parseResultState._2();
        assertTrue(parseResult instanceof ParseResult.Success<Calculator>);
        value = ((ParseResult.Success<Calculator>) parseResult).value();
        assertEquals(value, new Calculator.Bracket(new Calculator.Number(3456)));

        parseResultState = parser.parse("1 - 3 + 6 * 10 / 5");
        parseResult = parseResultState._2();
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

        parseResultState = parser.parse("(1 + 3) * 5");
        parseResult = parseResultState._2();
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
