package com.github.teruuu.jcombinator.example.program.ast;

public sealed interface Ast
        permits AstBool, AstChar, AstInt, AstString, Block, BinaryExpression, FunctionDefinition, FunctionCall, Identifier, IfExpression, Program, Assignment {
}
