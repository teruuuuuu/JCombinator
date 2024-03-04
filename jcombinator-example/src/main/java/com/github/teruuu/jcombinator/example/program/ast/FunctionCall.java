package com.github.teruuu.jcombinator.example.program.ast;

import java.util.List;

public record FunctionCall(String name, List<Ast> args) implements Ast {
}
