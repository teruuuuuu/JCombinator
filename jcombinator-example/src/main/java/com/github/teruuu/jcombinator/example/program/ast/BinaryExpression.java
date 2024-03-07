package com.github.teruuu.jcombinator.example.program.ast;

public record BinaryExpression(Operator operator, Ast lhs, Ast rhs) implements Ast {
}
