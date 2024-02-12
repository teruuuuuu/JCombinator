package com.github.teruuu.parser.toys.ast;

import java.util.List;
import java.util.Optional;

public sealed interface Expression {
    record BinaryExpression(Operator operator, Expression lhs, Expression rhs) implements Expression {
    }

    record IntegerLiteral(int value) implements Expression {
    }

    record Identifier(String name) implements Expression {
    }

    record FunctionCall(String name, List<Expression> args) implements Expression {
    }

    record BlockExpression(List<Expression> elements) implements Expression {
    }

    record Assignment(String name, Expression expression) implements Expression {
    }

    record WhileExpression(Expression condition, Expression body) implements Expression {
    }

    record IfExpression(Expression condition, Expression thenClause,
                        Optional<Expression> elseClause) implements Expression {
    }

    record Println(Expression arg) implements Expression {
    }

    record ArrayLiteral(List<Expression> items) implements Expression {
    }

    record BoolLiteral(boolean value) implements Expression {
    }

    record LabelledParameter(String name, Expression parameter) {
    }

    record LabelledCall(String name, List<LabelledParameter> args) implements Expression {
    }
}
