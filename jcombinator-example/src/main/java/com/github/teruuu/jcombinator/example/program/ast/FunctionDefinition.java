package com.github.teruuu.jcombinator.example.program.ast;

import java.util.List;

public record FunctionDefinition(String name, List<String> args, List<Ast> asts) implements Ast {
}
