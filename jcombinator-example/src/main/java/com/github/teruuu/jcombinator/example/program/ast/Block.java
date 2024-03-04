package com.github.teruuu.jcombinator.example.program.ast;

import java.util.List;

public record Block(List<Ast> asts) implements Ast {
}
