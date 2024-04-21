package com.github.teruuu.jcombinator.example.program.ast;


public enum Operator {
    ADD("add", "+"),
    SUBTRACT("subtract", "-"),
    MULTIPLY("multiply", "*"),
    DIVIDE("divide", "/"),
    SURPLUS("surplus", "%"),
    LESS_THAN("lessThan", "<"),
    LESS_EQUAL("lessEqual", "<="),
    GREATER_THAN("greaterThan", ">"),
    GREATER_EQUAL("greaterEqual", ">="),
    EQUAL("equal", "=="),
    AND("and", "&&"),
    BAR("bar", "||");

    private String name;
    private String symbol;

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }


    Operator(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }


}
