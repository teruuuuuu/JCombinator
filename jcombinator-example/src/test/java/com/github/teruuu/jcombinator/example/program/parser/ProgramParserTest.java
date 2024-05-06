package com.github.teruuu.jcombinator.example.program.parser;

import com.github.teruuu.jcombinator.core.parser.ParseContext;
import com.github.teruuu.jcombinator.core.parser.ParseError;
import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.type.Tuple;
import com.github.teruuu.jcombinator.example.program.ast.Assignment;
import com.github.teruuu.jcombinator.example.program.ast.Ast;
import com.github.teruuu.jcombinator.example.program.ast.AstInt;
import com.github.teruuu.jcombinator.example.program.ast.AstString;
import com.github.teruuu.jcombinator.example.program.ast.FunctionCall;
import com.github.teruuu.jcombinator.example.program.ast.Program;
import com.github.teruuu.jcombinator.example.program.interpreter.Interpreter;
import com.github.teruuu.jcombinator.example.program.value.Value;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProgramParserTest {

    @Test
    void test() {
        Interpreter interpreter = new Interpreter();
        Parser<Ast> parser = new ProgramParser();
        Tuple<ParseContext, ParseResult<Ast>> parseResultState = parser.parse("print(\"hello world.\")");
        ParseResult<Ast> parseResult = parseResultState._2();
        assert (parseResult instanceof ParseResult.Success<Ast>);
        Ast value = ((ParseResult.Success<Ast>) parseResult).value();
        assertEquals(value, new Program(List.of(new FunctionCall("print", List.of(new AstString("hello world."))))));


        parseResultState = parser.parse("var a = 1234");
        parseResult = parseResultState._2();
        assert (parseResult instanceof ParseResult.Success<Ast>);
        value = ((ParseResult.Success<Ast>) parseResult).value();
        assertEquals(value, new Program(List.of(new Assignment("a", new AstInt(1234)))));

        String programStr = """                
                def main() {
                    def fact(n, i) {
                        if(i > 0) {
                           n * fact(n, i - 1)
                        } else {
                            1
                        }
                    }
                    fact(5, 3)
                }
                """;
        parseResultState = parser.parse(programStr);
        parseResult = parseResultState._2();
        assert (parseResult instanceof ParseResult.Success<Ast>);
        value = ((ParseResult.Success<Ast>) parseResult).value();
        assertTrue(value instanceof Program);
        var result = interpreter.callMain((Program) value);
        assertEquals(Value.intValue(125), result);

        programStr = """         
                def fizzbuzz(n,i) {
                    if(n >= i){
                        if((i % 3 == 0) && (i % 5 == 0)){
                            print("fizzbuzz")
                        } else {
                            if(i % 3 == 0) {
                                print("fizz")
                            } else {
                                if(i % 5 == 0) {
                                    print("buzz")
                                } else {
                                    print(i)
                                }
                            }
                        }
                        fizzbuzz(n, i+1)
                    }
                }
                       
                def main() {
                    fizzbuzz(30,1)
                }
                """;
        parseResultState = parser.parse(programStr, ParseContext.context("program", 0));
        parseResult = parseResultState._2();
        assert (parseResult instanceof ParseResult.Success<Ast>);
        value = ((ParseResult.Success<Ast>) parseResult).value();
        assertTrue(value instanceof Program);
        result = interpreter.callMain((Program) value);
    }
}
