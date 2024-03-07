package com.github.teruuu.jcombinator.example.calculator;


public sealed interface Calculator {
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