package com.github.teruuu.jcombinator.example.program.interpreter;

import com.github.teruuu.jcombinator.example.program.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Scope(Map<String, Value> runtimeEnv, Map<String, Value> moduleEnv,
                    List<Map<String, Value>> functionEnvs) {

    public Optional<Map<String, Value>> findBinding(String name) {
        for (Map<String, Value> objectEnv : functionEnvs) {
            if (objectEnv.containsKey(name)) {
                return Optional.of(objectEnv);
            }
        }
        if (moduleEnv.containsKey(name)) {
            return Optional.of(moduleEnv);
        }
        if (runtimeEnv.containsKey(name)) {
            return Optional.of(runtimeEnv);
        }
        return Optional.empty();
    }

    public Map<String, Value> findVariableBinding(String name) {
        for (Map<String, Value> objectEnv : functionEnvs) {
            if (objectEnv.containsKey(name)) {
                return objectEnv;
            }
        }
        if (moduleEnv.containsKey(name)) {
            return moduleEnv;
        }
        return functionEnvs.getFirst();
    }

    public void addFuncEnv(Map<String, Value> env) {
        functionEnvs.addFirst(env);
    }

    public void addFuncEnv(String name, Value value) {
        functionEnvs.getFirst().put(name, value);
    }

    public void removeFuncEnv() {
        functionEnvs.removeFirst();
    }
}
