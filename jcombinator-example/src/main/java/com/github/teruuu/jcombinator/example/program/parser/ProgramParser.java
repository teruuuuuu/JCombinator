package com.github.teruuu.jcombinator.example.program.parser;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.ParserBase;
import com.github.teruuu.jcombinator.example.program.ast.Assignment;
import com.github.teruuu.jcombinator.example.program.ast.Ast;
import com.github.teruuu.jcombinator.example.program.ast.AstBool;
import com.github.teruuu.jcombinator.example.program.ast.AstInt;
import com.github.teruuu.jcombinator.example.program.ast.AstString;
import com.github.teruuu.jcombinator.example.program.ast.BinaryExpression;
import com.github.teruuu.jcombinator.example.program.ast.Block;
import com.github.teruuu.jcombinator.example.program.ast.FunctionCall;
import com.github.teruuu.jcombinator.example.program.ast.FunctionDefinition;
import com.github.teruuu.jcombinator.example.program.ast.Identifier;
import com.github.teruuu.jcombinator.example.program.ast.IfExpression;
import com.github.teruuu.jcombinator.example.program.ast.Operator;
import com.github.teruuu.jcombinator.example.program.ast.Program;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class ProgramParser implements Parser<Ast> {

    // symbol ::= [a-zA-Z][a-zA-Z0-9_]*
    final Parser<String> symbolParser = (
            Parser.range('a', 'z').or(Parser.range('A', 'Z'))
    ).and(
            Parser.range('a', 'z').or(Parser.range('A', 'Z')).or(Parser.range('0', '9')).or(Parser.literal('_')).seq0()
    ).flatMap(e -> (input, location) -> {
        String symbol = e._1() + String.join("", e._2());
        if (symbol.equals("if") || symbol.equals("else") || symbol.equals("var")
                || symbol.equals("{") || symbol.equals("}") || symbol.equals("<") || symbol.equals("<=")
                || symbol.equals(">") || symbol.equals(">=")) {
            return new ParseResult.Failure<>("yoyakugo", location);
        } else {
            return new ParseResult.Success<>(symbol, location);
        }
    }).withSkipSpace();

    // int ::= [0-9]+
    final Parser<Ast> intParser = Parser.integer().map(AstInt::new);

    // str ::= '"' [a-zA-Z] '"'
    final Parser<Ast> strParser = Parser.dQuoteString().map(AstString::new);

    // bool ::= true | false
    final Parser<Ast> boolParser = Parser.skip("true").pure((Ast) new AstBool(true)).
            or(Parser.skip("false").pure(new AstBool(false)));

    // identity ::= symbol
    final Parser<Ast> identityParser = symbolParser.map(Identifier::new);

    // PEG記法の場合callFuncの前半のルールがidentityと一致するのでcallFuncかどうかを先に見る
    // value ::= callFunc | int | str | bool | identity
    final Parser<Ast> valueParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return exprParser.or(callFuncParser).or(intParser).or(strParser).or(boolParser).or(identityParser);
        }
    };

    // callFunc ::= symbol '('　(value (',' value)*)? ')'
    final Parser<Ast> callFuncParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return symbolParser.and(
                    Parser.array(
                            Parser.literal("(").withSkipSpace()
                            , Parser.literal(")").withSkipSpace()
                            , Parser.literal(",").withSkipSpace()
                            , valueParser
                    )
            ).map(v -> new FunctionCall(v._1(), v._2()));
        }
    };

    // assign ::= symbol '=' value
    final Parser<Ast> assignParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return Parser.literal("var").withSkipSpace().flatMap(var ->
                    Parser.space().andRight(symbolParser).flatMap(symbol ->
                            Parser.literal("=").withSkipSpace().flatMap(eq ->
                                    valueParser.withSkipSpace().map(value ->
                                            new Assignment(symbol, value)
                                    )
                            )
                    )
            );
        }
    };

    // line = (expr|assign|callFunc|funcDef)* value
    final Parser<List<Ast>> lineParser = new ParserBase<>() {
        @Override
        protected Parser<List<Ast>> genParser() {
            return (
                    exprParser
                            .or(assignParser)
                            .or(callFuncParser)
                            .or(funcDefParser)
                            .withSkipSpace().seq0()
            ).and(
                    valueParser.withSkipSpace().optional()
            ).map(then ->
                    Stream.concat(then._1().stream(), then._2().stream()).toList()
            );
        }
    };

    // expr ::= mathExpr | if
    final Parser<Ast> exprParser = new ParserBase<Ast>() {
        @Override
        protected Parser<Ast> genParser() {
            return ifParser
                    .or(lessThanParser).or(lessEqualParser)
                    .or(greaterThanParser).or(greaterEqualParser)
                    .or(equalParser).or(andParser).or(barParser)
                    .or(mathExprParser);
        }
    };

    // mathExpr ::= term (('+'|'-') term)*
    final Parser<Ast> mathExprParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            Parser<Function<Ast, Ast>> addGennParser = Parser.skip('+').withSkipSpace().and(termParser).map(value ->
                    v -> new BinaryExpression(Operator.ADD, v, value._2()));
            Parser<Function<Ast, Ast>> subGennParser = Parser.skip('-').withSkipSpace().and(termParser).map(value ->
                    v -> new BinaryExpression(Operator.SUBTRACT, v, value._2()));
            return termParser.and(addGennParser.or(subGennParser).seq0()).map(value ->
                    foldLeft(value._1(), value._2(), (a, b) -> b.apply(a)));
        }
    };

    // term ::= factor ( ('*'|'/') factor)*
    final Parser<Ast> termParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            Parser<Function<Ast, Ast>> addGennParser = Parser.skip('*').withSkipSpace().and(factorParser).map(value ->
                    v -> new BinaryExpression(Operator.MULTIPLY, v, value._2()));
            Parser<Function<Ast, Ast>> subGennParser = Parser.skip('/').withSkipSpace().and(factorParser).map(value ->
                    v -> new BinaryExpression(Operator.DIVIDE, v, value._2()));
            return factorParser.and(addGennParser.or(subGennParser).seq0()).map(value ->
                    foldLeft(value._1(), value._2(), (a, b) -> b.apply(a)));
        }
    };

    // factor ::= '(' mathExpr ')' | (callFunc|int|str|identity)
    final Parser<Ast> factorParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return (
                    Parser.skip('(').withSkipSpace()
                            .andRight(mathExprParser.withSkipSpace())
                            .andLeft(Parser.skip(')').withSkipSpace()
                            )
            ).or(callFuncParser.or(intParser).or(strParser).or(identityParser).withSkipSpace());
        }
    };

    final Parser<Ast> evalParser = callFuncParser.or(identityParser).or(intParser).or(strParser).withSkipSpace();

    final Parser<Ast> lessThanParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return evalParser.flatMap(lhs ->
                    Parser.literal("<").withSkipSpace().flatMap(symbol ->
                            evalParser.map(rhs ->
                                    new BinaryExpression(Operator.LESS_THAN, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> lessEqualParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return evalParser.flatMap(lhs ->
                    Parser.skip("<=").withSkipSpace().flatMap(symbol ->
                            evalParser.map(rhs ->
                                    new BinaryExpression(Operator.LESS_EQUAL, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> greaterThanParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return evalParser.flatMap(lhs ->
                    Parser.skip(">").withSkipSpace().flatMap(symbol ->
                            evalParser.map(rhs ->
                                    new BinaryExpression(Operator.GREATER_THAN, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> greaterEqualParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return evalParser.flatMap(lhs ->
                    Parser.skip(">=").withSkipSpace().flatMap(symbol ->
                            evalParser.map(rhs ->
                                    new BinaryExpression(Operator.GREATER_EQUAL, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> equalParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return callFuncParser.or(intParser).withSkipSpace().flatMap(lhs ->
                    Parser.skip("==").withSkipSpace().flatMap(symbol ->
                            callFuncParser.or(intParser).withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.EQUAL, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> andParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return callFuncParser.or(intParser).withSkipSpace().flatMap(lhs ->
                    Parser.skip("&&").withSkipSpace().flatMap(symbol ->
                            callFuncParser.or(intParser).withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.AND, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> barParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return callFuncParser.or(intParser).withSkipSpace().flatMap(lhs ->
                    Parser.skip("||").withSkipSpace().flatMap(symbol ->
                            callFuncParser.or(intParser).withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.BAR, lhs, rhs)
                            )
                    )
            );
        }
    };

    // if ::= "if" '(' value ')' '{' (assign|callFunc|funcDef)* value? '}' ("else" '{' (assign|callFunc|funcDef)* value? '}')?
    final Parser<Ast> ifParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return Parser.skip("if").withSkipSpace().andLeft(Parser.literal('(')).withSkipSpace().flatMap(codStart ->
                    valueParser.withSkipSpace().flatMap(cond ->
                            Parser.literal(')').withSkipSpace().flatMap(condEnd ->
                                    Parser.literal('{').withSkipSpace().andRight(
                                            lineParser.withSkipSpace().map(Block::new)
                                    ).andLeft(Parser.literal('}').withSkipSpace()).flatMap(thenCond ->
                                            (
                                                    Parser.literal("else").withSkipSpace().andRight(Parser.literal('{').withSkipSpace()).andRight(
                                                            lineParser.withSkipSpace().map(v -> (Ast) new Block(v))
                                                    ).andLeft(Parser.literal('}').withSkipSpace())
                                            ).optional().map(elseCondOpt ->
                                                    new IfExpression(cond, thenCond, elseCondOpt)
                                            )
                                    )
                            )
                    )
            );
        }
    };

    // funcDef ::= "def" identity '(' (identity (',' identity)*)? ')' '{' (assign|callFunc|funcDef)* value? '}'
    final Parser<Ast> funcDefParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return Parser.literal("def").withSkipSpace().flatMap(def ->
                    Parser.space().andRight(symbolParser).flatMap(symbol ->
                            Parser.array(
                                    Parser.literal('(').withSkipSpace(),
                                    Parser.literal(')').withSkipSpace(),
                                    Parser.literal(','),
                                    symbolParser
                            ).flatMap(args ->
                                    Parser.literal('{').withSkipSpace().andRight(
                                            lineParser.withSkipSpace()
                                    ).andLeft(Parser.literal('}').withSkipSpace()).map(line ->
                                            new FunctionDefinition(
                                                    symbol,
                                                    args,
                                                    line
                                            )
                                    )
                            )
                    )
            );
        }
    };

    // program ::= (assign|funcDef|callFunc)*
    private final Parser<Ast> parser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            Parser<Ast> p = assignParser.or(funcDefParser).or(callFuncParser).withSkipSpace().seq0().map(Program::new);
            return p.andLeft(Parser.end().withSkipSpace());
        }
    };

    public ProgramParser() {
    }

    @Override
    public ParseResult<Ast> parse(String input, int location) {
        return parser.parse(input, location);
    }

    private <X, Y> X foldLeft(X x, List<Y> list, BiFunction<X, Y, X> func) {
        for (Y y : list) {
            x = func.apply(x, y);
        }
        return x;
    }
}
