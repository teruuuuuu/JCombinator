package com.github.teruuu.jcombinator.core.parser;

public record ParseResultState<T>(ParseContext context, ParseResult<T> parseResult) {
}
