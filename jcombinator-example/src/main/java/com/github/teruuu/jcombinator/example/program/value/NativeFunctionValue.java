package com.github.teruuu.jcombinator.example.program.value;

import java.util.List;
import java.util.function.Function;

public record NativeFunctionValue(String name, Function<List<Value>, Value> function) implements Value {

    public String toString() {
        return String.format("NativeFunction<name=%s, >", name);
    }
}
