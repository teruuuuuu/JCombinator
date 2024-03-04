package com.github.teruuu.jcombinator.example.program.ast;

import java.util.List;

public record Program(List<Ast> definitions) implements Ast {
}
