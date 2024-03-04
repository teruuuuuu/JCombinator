package com.github.teruuu.jcombinator.example.program;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.example.program.ast.Ast;
import com.github.teruuu.jcombinator.example.program.ast.Program;
import com.github.teruuu.jcombinator.example.program.interpreter.Interpreter;
import com.github.teruuu.jcombinator.example.program.parser.ProgramParser;

import java.util.Objects;

/**
 * https://github.com/kmizu/toys
 */
public class Main {


    /**
     * ToysのPEG
     * <p>
     * program <- topLevelDefinition*;
     * <p>
     * lines <- line+;
     * <p>
     * // 「トップレベル」の定義
     * topLevelDefinition <-
     * globalVariableDefinition / functionDefinition;
     * <p>
     * // 関数定義の文法
     * functionDefinition <-
     * "define" identifier
     * "(" (identifier ("," identifier)*)? ")"
     * blockExpression;
     * <p>
     * // グローバル変数定義の文法
     * globalVariableDefinition <-
     * "global" identifier "=" expression;
     * <p>
     * // 「行」の定義
     * line <- println / whileExpression / ifExpression
     * / assignment / expressionLine / blockExpression;
     * <p>
     * // println式
     * println <- "println" "(" expression ")"
     * <p>
     * // if式
     * ifExpression <-
     * "if" "(" expression ")" line ("else" line)?;
     * <p>
     * // while式
     * whileExpression <-
     * "while" "(" expression ")" line;
     * <p>
     * // ブロック式
     * blockExpression <- "{" line* "}";
     * <p>
     * // 代入式
     * assignment <- identifier "=" expression ";";
     * <p>
     * // 「行」式
     * expressionLine <- expression ";";
     * <p>
     * // 「式」の定義
     * expression <- comparative;
     * <p>
     * comparative <- additive (
     * ("<" / ">" / "<=" / ">=" / "==") additive
     * )*;
     * <p>
     * additive <- multitive (
     * ("+" / "-") multitive
     * )*;
     * <p>
     * multitive <- primary (
     * ("*" / "/") primary
     * )*;
     * <p>
     * primary <- "(" expression ")"
     * / integer
     * / identifier;
     * <p>
     * functionCall <- identifier "("
     * (expression ("," expression)*)?
     * ")";
     * <p>
     * identifier <- IDENT
     */

    static void run(String input) {
        Parser<Ast> parser = new ProgramParser();
        switch (parser.parse(input)) {
            case ParseResult.Success<Ast> success -> {

                if (Objects.requireNonNull(success.value()) instanceof Program program) {
                    Interpreter interpreter = new Interpreter();
                    interpreter.callMain(program);
                } else {
                    throw new RuntimeException("");
                }

            }
            case ParseResult.Failure<Ast> failure -> {
                throw new RuntimeException("compile failed:" + failure.message());
            }
        }
    }
}

