package com.github.teruuu.parser.toys.ast;

import com.github.teruuu.parser.toys.exception.LanguageException;

import java.util.List;
import java.util.Map;

public sealed interface Value {

    record Int(int value) implements Value {
    }

    record Array(List<? extends Value> values) implements Value {
    }

    record Dictionary(Map<? extends Value, ? super Value> entries) implements Value {
    }

    record Bool(boolean value) implements Value {
    }

    static Value wrap(Object javaValue) {
        if (javaValue instanceof Integer v) return new Int(v);
        if (javaValue instanceof Boolean v) return new Bool(v);
        if (javaValue instanceof List<?> v) return new Array((List<Value>) v);
        if (javaValue instanceof Map<?, ?> v) return new Dictionary((Map<Value, Value>) v);
        throw new LanguageException("must not reach here");
    }
}
