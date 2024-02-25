package com.github.teruuu.jcombinator.example.json;

import com.github.teruuu.jcombinator.core.parser.type.Either;

import java.util.List;
import java.util.Map;

public sealed interface Json {
    record JArray(List<Json> value) implements Json {
    }

    record JBoolean(boolean value) implements Json {
    }

    record JNull() implements Json {
    }

    record JNumber(Either<Integer, Double> value) implements Json {
    }

    record JObject(Map<String, Json> value) implements Json {
    }

    record JString(String value) implements Json {
    }

    static Json jArray(List<Json> value) {
        return new JArray(value);
    }

    static Json jBoolean(boolean value) {
        return new JBoolean(value);
    }

    static Json jNull() {
        return new JNull();
    }

    static Json jNumber(Either<Integer, Double> value) {
        return new JNumber(value);
    }

    static Json jObject(Map<String, Json> value) {
        return new JObject(value);
    }

    static Json jString(String value) {
        return new JString(value);
    }
}