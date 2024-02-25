package com.github.teruuu.parser.toys.interpreter;

import com.github.teruuu.parser.toys.ast.*;
import com.github.teruuu.parser.toys.exception.LanguageException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.teruuu.parser.toys.ast.Environment.newEnvironment;
import static com.github.teruuu.parser.toys.ast.Value.wrap;

public class Interpreter {

    private final Environment variableEnvironment;
    private final Map<String, TopLevel.FunctionDefinition> functionEnvironment;

    public Interpreter() {
        this.variableEnvironment = newEnvironment(Optional.empty());
        this.functionEnvironment = new HashMap<>();
    }

    public Value interpret(Expression expression) {
        switch (expression) {
            case Expression.ArrayLiteral arrayLiteral -> {

            }
            case Expression.Assignment assignment -> {

            }
            case Expression.BinaryExpression binaryExpression -> {

            }
            case Expression.BlockExpression blockExpression -> {

            }
            case Expression.BoolLiteral boolLiteral -> {

            }
            case Expression.Identifier identifier -> {

            }
            case Expression.FunctionCall functionCall -> {

            }
            case Expression.IfExpression ifExpression -> {

            }
            case Expression.Println println -> {
                return interpret(println.arg());
            }
            case Expression.IntegerLiteral integerLiteral -> {
                return wrap(integerLiteral.value());
            }
            case Expression.WhileExpression whileExpression -> {

            }
            case Expression.LabelledCall labelledCall -> {

            }
        }
        throw new RuntimeException("must not reach here");
    }

    public Value callMain(Program program) {
        var topLevels = program.definitions();
        for (var topLevel : topLevels) {
            switch (topLevel) {
                case TopLevel.GlobalVariableDefinition globalVariableDefinition -> {
                    variableEnvironment.bindings().put(
                            globalVariableDefinition.name(),
                            interpret(globalVariableDefinition.expression())
                    );
                }
                case TopLevel.FunctionDefinition functionDefinition -> {
                    functionEnvironment.put(functionDefinition.name(), functionDefinition);
                }
            }
        }
        var mainFunction = functionEnvironment.get("main");
        if (mainFunction != null) {
            return interpret(mainFunction.body());
        } else {
            throw new LanguageException("This program doesn't have main function");
        }
    }
}
