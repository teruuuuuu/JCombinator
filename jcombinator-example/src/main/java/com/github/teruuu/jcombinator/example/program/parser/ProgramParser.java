package com.github.teruuu.jcombinator.example.program.parser;

import com.github.teruuu.jcombinator.core.parser.ParseError;
import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.ParserBase;
import com.github.teruuu.jcombinator.core.parser.SkipSpaceParser;
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

import java.util.ArrayList;
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
            return new ParseResult.Failure<>(new ParseError("symbol", "yoyakugo", location, List.of()), location);
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
    // value ::= expr | callFunc | int | str | bool | identity
    final Parser<Ast> valueParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return expressionParser.or(callFuncParser)
                    .or(intParser).or(strParser)
                    .or(boolParser).or(identityParser)
                    .withSkipSpace();
        }
    };

    // callFunc ::= symbol '('　(value (',' value)*)? ')'
    final Parser<Ast> callFuncParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return symbolParser.withSkipSpace().and(
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
                                    valueParser.map(value ->
                                            new Assignment(symbol, value)
                                    )
                            )
                    )
            );
        }
    };

    // line = (expr|assign|callFunc|funcDef)* value
    final Parser<List<Ast>> linesParser = new ParserBase<>() {
        @Override
        protected Parser<List<Ast>> genParser() {
            return (
                    lineParser.seq0()
            ).and(
                    valueParser.optional()
            ).map(then ->
                    Stream.concat(then._1().stream(), then._2().stream()).toList()
            );
        }
    };

    // expr ::= if | expression
    final Parser<Ast> lineParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return ifParser
                    .or(funcDefParser)
                    .or(expressionParser)
                    .withSkipSpace();
        }
    };

    // mathExpr ::= term (('+'|'-') term)*
    final Parser<Ast> additiveParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            Parser<Function<Ast, Ast>> addGennParser = Parser.skip('+').withSkipSpace().and(multitiveParser).map(value ->
                    v -> new BinaryExpression(Operator.ADD, v, value._2()));
            Parser<Function<Ast, Ast>> subGennParser = Parser.skip('-').withSkipSpace().and(multitiveParser).map(value ->
                    v -> new BinaryExpression(Operator.SUBTRACT, v, value._2()));
            return multitiveParser.and(addGennParser.or(subGennParser).seq0()).map(value ->
                    foldLeft(value._1(), value._2(), (a, b) -> b.apply(a)));
        }
    };

    // term ::= factor ( ('*'|'/'|'%') factor)*
    final Parser<Ast> multitiveParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            Parser<Function<Ast, Ast>> addGennParser = Parser.skip('*').withSkipSpace().and(primaryParser).map(value ->
                    v -> new BinaryExpression(Operator.MULTIPLY, v, value._2()));
            Parser<Function<Ast, Ast>> subGennParser = Parser.skip('/').withSkipSpace().and(primaryParser).map(value ->
                    v -> new BinaryExpression(Operator.DIVIDE, v, value._2()));
            Parser<Function<Ast, Ast>> subtractGenParser = Parser.skip('%').withSkipSpace().and(primaryParser).map(value ->
                    v -> new BinaryExpression(Operator.SURPLUS, v, value._2()));
            return primaryParser.and(addGennParser.or(subGennParser).or(subtractGenParser).seq0()).map(value ->
                    foldLeft(value._1(), value._2(), (a, b) -> b.apply(a)));
        }
    };

    // factor ::= '(' mathExpr ')' | (callFunc|int|str|identity)
    final Parser<Ast> primaryParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return (
                    Parser.skip('(').withSkipSpace()
                            .andRight(expressionParser.withSkipSpace())
                            .andLeft(Parser.skip(')').withSkipSpace()
                            )
            ).or(callFuncParser.or(intParser).or(strParser).or(identityParser).withSkipSpace());
        }
    };

    final Parser<Ast> logicParer = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return barParser.or(andParser)
                    .or(expressionParser)
                    .withSkipSpace();
        }
    };

    //    // value ::= expr | callFunc | int | str | bool | identity
    //    // eval ::= callFunc | int | str | bool | identity
    //    final Parser<Ast> evalParser = callFuncParser.or(identityParser).or(intParser).or(strParser).withSkipSpace();

    final Parser<Ast> expressionParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return barParser.or(andParser).or(lessThanParser).or(lessEqualParser)
                    .or(greaterThanParser).or(greaterEqualParser)
                    .or(equalParser).or(additiveParser)
                    .withSkipSpace();
        }
    };

    final Parser<Ast> lessThanParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return additiveParser.withSkipSpace().flatMap(lhs ->
                    Parser.literal("<").withSkipSpace().flatMap(symbol ->
                            additiveParser.withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.LESS_THAN, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> lessEqualParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return additiveParser.withSkipSpace().flatMap(lhs ->
                    Parser.skip("<=").withSkipSpace().flatMap(symbol ->
                            additiveParser.withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.LESS_EQUAL, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> greaterThanParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return additiveParser.withSkipSpace().flatMap(lhs ->
                    Parser.skip(">").withSkipSpace().flatMap(symbol ->
                            additiveParser.withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.GREATER_THAN, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> greaterEqualParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return additiveParser.withSkipSpace().flatMap(lhs ->
                    Parser.skip(">=").withSkipSpace().flatMap(symbol ->
                            additiveParser.withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.GREATER_EQUAL, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> equalParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return additiveParser.or(intParser).withSkipSpace().flatMap(lhs ->
                    Parser.skip("==").withSkipSpace().flatMap(symbol ->
                            additiveParser.withSkipSpace().or(intParser).withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.EQUAL, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> andParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return additiveParser.or(intParser).withSkipSpace().flatMap(lhs ->
                    Parser.skip("&&").withSkipSpace().flatMap(symbol ->
                            additiveParser.withSkipSpace().or(intParser).withSkipSpace().map(rhs ->
                                    new BinaryExpression(Operator.AND, lhs, rhs)
                            )
                    )
            );
        }
    };

    final Parser<Ast> barParser = new ParserBase<>() {
        @Override
        protected Parser<Ast> genParser() {
            return additiveParser.or(intParser).withSkipSpace().flatMap(lhs ->
                    Parser.skip("||").withSkipSpace().flatMap(symbol ->
                            additiveParser.or(intParser).withSkipSpace().map(rhs ->
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
                    expressionParser.withSkipSpace().flatMap(cond ->
                            Parser.literal(')').withSkipSpace().flatMap(condEnd ->
                                    Parser.literal('{').withSkipSpace().andRight(
                                            linesParser.map(Block::new)
                                    ).andLeft(Parser.literal('}').withSkipSpace()).flatMap(thenCond ->
                                            (
                                                    Parser.literal("else").withSkipSpace().andRight(Parser.literal('{').withSkipSpace()).andRight(
                                                            linesParser.map(v -> (Ast) new Block(v))
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
                                            linesParser
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

            Parser<Ast> topLevelParser = assignParser.or(funcDefParser).or(callFuncParser).withSkipSpace();
            Parser<Void> skipSpace = new SkipSpaceParser();
            return new Parser<Ast>() {

                @Override
                public ParseResult<Ast> parse(String input, int location) {
                    List<Ast> asts = new ArrayList<>();
                    while (true) {
                        ParseResult<Void> skipSpaceResult = skipSpace.parse(input, location);
                        if (skipSpaceResult instanceof ParseResult.Failure<Void>) {
                            break;
                        }
                        location = ((ParseResult.Success<Void>) skipSpaceResult).location();
                        if (location == input.length()) {
                            break;
                        }

                        switch (topLevelParser.parse(input, location)) {
                            case ParseResult.Success<Ast> success -> {
                                asts.add(success.value());
                                location = success.location();
                            }
                            case ParseResult.Failure<Ast> failure -> {
                                return new ParseResult.Failure<>(new ParseError("program", "failed", location, List.of(failure.parseError())), location);
                            }
                        }
                    }
                    return new ParseResult.Success<>(new Program(asts), location);
                }
            };
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
