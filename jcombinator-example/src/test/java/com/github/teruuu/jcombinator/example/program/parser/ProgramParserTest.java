package com.github.teruuu.jcombinator.example.program.parser;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
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
        ParseResult<Ast> parseResult = parser.parse("print(\"hello world.\")");
        assert (parseResult instanceof ParseResult.Success<Ast>);
        Ast value = ((ParseResult.Success<Ast>) parseResult).value();
        assertEquals(value, new Program(List.of(new FunctionCall("print", List.of(new AstString("hello world."))))));


        parseResult = parser.parse("var a = 1234");
        assert (parseResult instanceof ParseResult.Success<Ast>);
        value = ((ParseResult.Success<Ast>) parseResult).value();
        assertEquals(value, new Program(List.of(new Assignment("a", new AstInt(1234)))));


        String programStr = """                
                def fact(n, i) {
                    if(i > 0) {
                       n * fact(n, i - 1)
                    } else {
                        1
                    }
                }

                def main() {
                    fact(5, 3)
                }
                """;
        parseResult = parser.parse(programStr);
        assert (parseResult instanceof ParseResult.Success<Ast>);
        value = ((ParseResult.Success<Ast>) parseResult).value();
        assertTrue(value instanceof Program);
        var result = interpreter.callMain((Program) value);
        assertEquals(Value.intValue(125), result);
    }
}
