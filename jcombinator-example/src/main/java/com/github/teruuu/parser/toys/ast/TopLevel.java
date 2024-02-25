package com.github.teruuu.parser.toys.ast;

import java.util.List;

public sealed interface TopLevel {
    record GlobalVariableDefinition(String name, Expression expression) implements TopLevel {
    }

    record FunctionDefinition(String name, List<String> args, Expression body) implements TopLevel {
    }
}
