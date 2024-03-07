package com.github.teruuu.jcombinator.example.program.value;

import com.github.teruuu.jcombinator.example.program.ast.FunctionDefinition;

import java.util.stream.Collectors;

public record FunctionValue(FunctionDefinition functionDefinition) implements Value {

    public static FunctionValue of(FunctionDefinition functionDefinition) {
        return new FunctionValue(functionDefinition);
    }

    public String toString() {
        return String.format("Function<name=%s, args=%s>", functionDefinition.name(), functionDefinition.args().stream().collect(Collectors.joining(",", "[", "]")));
    }
}
