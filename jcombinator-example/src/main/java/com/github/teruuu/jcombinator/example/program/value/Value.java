package com.github.teruuu.jcombinator.example.program.value;

import com.github.teruuu.jcombinator.example.program.ast.FunctionDefinition;

import java.util.List;
import java.util.function.Function;

public sealed interface Value
        permits Array, Bool, Char, Dictionary, FunctionValue, Int, NativeFunctionValue, Str, Void {


    //    static Value wrap(Object javaValue) {
    //        if (javaValue instanceof Integer v) return new Int(v);
    //        if (javaValue instanceof String v) return new Str(v);
    //        if (javaValue instanceof Boolean v) return new Bool(v);
    ////        if (javaValue instanceof List<?> v) return new Array((List<Value>) v);
    ////        if (javaValue instanceof Map<?, ?> v) return new Dictionary((Map<Value, Value>) v);
    //        throw new LanguageException("must not reach here");
    //    }

    String toString();

    static Bool bool(boolean v) {
        return new Bool(v);
    }

    static Char charValue(char v) {
        return new Char(v);
    }

    static FunctionValue functionValue(FunctionDefinition v) {
        return new FunctionValue(v);
    }

    static Int intValue(int v) {
        return new Int(v);
    }

    static NativeFunctionValue nativeFunctionValue(String name, Function<List<Value>, Value> function) {
        return new NativeFunctionValue(name, function);
    }

    static Str str(String v) {
        return new Str(v);
    }

    static Void voidValue() {
        return new Void();
    }
}
