package com.github.teruuu.jcombinator.example.program.interpreter;

import com.github.teruuu.jcombinator.example.program.ast.Assignment;
import com.github.teruuu.jcombinator.example.program.ast.AstInt;
import com.github.teruuu.jcombinator.example.program.ast.AstString;
import com.github.teruuu.jcombinator.example.program.ast.BinaryExpression;
import com.github.teruuu.jcombinator.example.program.ast.FunctionCall;
import com.github.teruuu.jcombinator.example.program.ast.FunctionDefinition;
import com.github.teruuu.jcombinator.example.program.ast.Identifier;
import com.github.teruuu.jcombinator.example.program.ast.Operator;
import com.github.teruuu.jcombinator.example.program.ast.Program;
import org.junit.jupiter.api.Test;

import java.util.List;


public class InterpreterTest {

    @Test
    public void test() {
        var interpreter = new Interpreter();
        var program = new Program(List.of(
                new FunctionDefinition(
                        "main", List.of(),
                        List.of(
                                new FunctionCall(
                                        "print",
                                        List.of(new AstString("hello world.")))
                        )
                )
        ));
        interpreter.callMain(program);

        program = new Program(List.of(
                new FunctionDefinition(
                        "main", List.of(),
                        List.of(
                                new FunctionCall(
                                        "print",
                                        List.of(new BinaryExpression(
                                                Operator.ADD,
                                                new AstInt(12345),
                                                new AstInt(67890)
                                        )))
                        )
                )
        ));
        interpreter.callMain(program);

        program = new Program(List.of(
                new FunctionDefinition(
                        "main", List.of(),
                        List.of(
                                new FunctionCall(
                                        "print",
                                        List.of(new BinaryExpression(
                                                Operator.ADD,
                                                new AstInt(12345),
                                                new AstString("abcde")
                                        )))
                        )
                )
        ));
        interpreter.callMain(program);

        program = new Program(List.of(
                new FunctionDefinition(
                        "main", List.of(),
                        List.of(
                                new Assignment("a", new AstString("assignment")),
                                new FunctionCall(
                                        "print",
                                        List.of(new Identifier("a")))
                        )
                )
        ));
        interpreter.callMain(program);

    }
}
