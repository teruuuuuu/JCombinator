package com.github.teruuu.parser.toys.parser;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.parser.toys.ast.Expression;
import com.github.teruuu.parser.toys.ast.Program;

public class ToysParser implements Parser<Program> {

    private final Parser<Program> parser;

    public ToysParser() {
        Parser<Void> LPAREN = Parser.skip('(').withSkipSpace();
        Parser<Void> RPAREN = Parser.skip(')').withSkipSpace();
        Parser<Void> SEMI_COLON = Parser.skip(';').withSkipSpace();
        Parser<Void> PRINTLN = Parser.skip("println").withSkipSpace();

        final Parser<Expression> expression = null;
        final Parser<Expression> println = PRINTLN.andRight(expression.between(LPAREN, RPAREN).andLeft(SEMI_COLON));


        this.parser = null;
    }


    @Override
    public ParseResult<Program> parse(String input, int location) {
        return parser.parse(input, location);
    }
}
