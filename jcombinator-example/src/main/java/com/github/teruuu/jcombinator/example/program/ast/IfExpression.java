package com.github.teruuu.jcombinator.example.program.ast;

import java.util.Optional;

public record IfExpression(Ast condition, Ast thenClause, Optional<Ast> elseClause) implements Ast {
}
