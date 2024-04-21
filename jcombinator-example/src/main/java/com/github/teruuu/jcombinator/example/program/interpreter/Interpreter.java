package com.github.teruuu.jcombinator.example.program.interpreter;

import com.github.teruuu.jcombinator.example.program.ast.Assignment;
import com.github.teruuu.jcombinator.example.program.ast.Ast;
import com.github.teruuu.jcombinator.example.program.ast.AstBool;
import com.github.teruuu.jcombinator.example.program.ast.AstChar;
import com.github.teruuu.jcombinator.example.program.ast.AstInt;
import com.github.teruuu.jcombinator.example.program.ast.AstString;
import com.github.teruuu.jcombinator.example.program.ast.BinaryExpression;
import com.github.teruuu.jcombinator.example.program.ast.Block;
import com.github.teruuu.jcombinator.example.program.ast.FunctionCall;
import com.github.teruuu.jcombinator.example.program.ast.FunctionDefinition;
import com.github.teruuu.jcombinator.example.program.ast.Identifier;
import com.github.teruuu.jcombinator.example.program.ast.IfExpression;
import com.github.teruuu.jcombinator.example.program.ast.Program;
import com.github.teruuu.jcombinator.example.program.exception.LanguageException;
import com.github.teruuu.jcombinator.example.program.value.Bool;
import com.github.teruuu.jcombinator.example.program.value.Char;
import com.github.teruuu.jcombinator.example.program.value.FunctionValue;
import com.github.teruuu.jcombinator.example.program.value.Int;
import com.github.teruuu.jcombinator.example.program.value.NativeFunctionValue;
import com.github.teruuu.jcombinator.example.program.value.Str;
import com.github.teruuu.jcombinator.example.program.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class Interpreter {

    public Interpreter() {
    }

    public Value interpret(Ast ast, Scope scope) {
        switch (ast) {
            case Assignment assignment -> {
                String name = assignment.name();
                Value value = interpret(assignment.ast(), scope);
                scope.findVariableBinding(name).put(name, value);
                return value;
            }
            case AstBool astBool -> {
                return Value.bool(astBool.value());
            }
            case AstChar astChar -> {
                return Value.charValue(astChar.value());
            }
            case AstInt astInt -> {
                return Value.intValue(astInt.value());
            }
            case AstString string -> {
                return Value.str(string.value());
            }
            case BinaryExpression binaryExpression -> {
                switch (binaryExpression.operator()) {
                    case ADD -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.intValue(((Int) lvalue).value() + ((Int) rvalue).value());
                        } else {
                            return Value.str(lvalue.toString() + rvalue.toString());
                        }
                    }
                    case SUBTRACT -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.intValue(((Int) lvalue).value() - ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("subtract invalid argument[%s - %s].", lvalue, rvalue));
                        }
                    }
                    case MULTIPLY -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.intValue(((Int) lvalue).value() * ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("multiply invalid argument[%s * %s].", lvalue, rvalue));
                        }
                    }
                    case DIVIDE -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.intValue(((Int) lvalue).value() / ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("divide invalid argument[%s / %s].", lvalue, rvalue));
                        }
                    }
                    case SURPLUS -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.intValue(((Int) lvalue).value() % ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("surplus invalid argument[%s å %s].", lvalue, rvalue));
                        }
                    }
                    case LESS_THAN -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.bool(((Int) lvalue).value() < ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("lessThan invalid argument[%s < %s].", lvalue, rvalue));
                        }
                    }
                    case LESS_EQUAL -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.bool(((Int) lvalue).value() <= ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("lessEqual invalid argument[%s <= %s].", lvalue, rvalue));
                        }
                    }
                    case GREATER_THAN -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.bool(((Int) lvalue).value() > ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("greaterThan invalid argument[%s > %s].", lvalue, rvalue));
                        }
                    }
                    case GREATER_EQUAL -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.bool(((Int) lvalue).value() >= ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("greaterEqual invalid argument[%s >= %s].", lvalue, rvalue));
                        }
                    }
                    case EQUAL -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.bool(((Int) lvalue).value() == ((Int) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("equal invalid argument[%s == %s].", lvalue, rvalue));
                        }
                    }
                    case AND -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Bool && rvalue instanceof Bool) {
                            return Value.bool(((Bool) lvalue).value() && ((Bool) rvalue).value());
                        } else if (lvalue instanceof Int && rvalue instanceof Int) {
                            return Value.bool(((Int) lvalue).value() > 0 &&  ((Int) rvalue).value() > 0);
                        } else {
                            throw new LanguageException(String.format("and invalid argument[%s && %s].", lvalue, rvalue));
                        }
                    }
                    case BAR -> {
                        var lvalue = interpret(binaryExpression.lhs(), scope);
                        var rvalue = interpret(binaryExpression.rhs(), scope);
                        if (lvalue instanceof Bool && rvalue instanceof Bool) {
                            return Value.bool(((Bool) lvalue).value() || ((Bool) rvalue).value());
                        } else {
                            throw new LanguageException(String.format("bar invalid argument[%s || %s].", lvalue, rvalue));
                        }
                    }
                }
            }
            case Block block -> {
                scope.addFuncEnv(new HashMap<>());
                Value ret = null;
                for (Ast exp : block.asts()) {
                    ret = interpret(exp, scope);
                }
                scope.removeFuncEnv();
                return ret;
            }

            case FunctionCall functionCall -> {
                String name = functionCall.name();
                Optional<Map<String, Value>> bindingOpt = scope.findBinding(name);
                if (bindingOpt.isEmpty()) {
                    throw new LanguageException("Function " + functionCall.name() + " is not found");
                } else {
                    Value value = bindingOpt.get().get(name);
                    if (value instanceof FunctionValue function) {
                        var funcDefinition = function.functionDefinition();
                        Map<String, Value> args = new HashMap<>();
                        var actualParams = functionCall.args();
                        var formalParams = funcDefinition.args();
                        for (int i = 0; i < formalParams.size(); i++) {
                            args.put(formalParams.get(i), interpret(actualParams.get(i), scope));
                        }
                        scope.addFuncEnv(args);
                        Value ret = null;
                        for (Ast program : funcDefinition.asts()) {
                            ret = interpret(program, scope);
                        }
                        scope.removeFuncEnv();
                        return ret;
                    } else if (value instanceof NativeFunctionValue functionValue) {
                        return functionValue.function().apply(functionCall.args().stream().map(arg -> interpret(arg, scope)).toList());
                    } else {
                        throw new LanguageException("Function " + functionCall.name() + " is not function");
                    }
                }
            }
            case FunctionDefinition functionDefinition -> {
                scope.addFuncEnv(functionDefinition.name(), new FunctionValue(functionDefinition));
                return null;
            }
            case Identifier identifier -> {
                String name = identifier.name();
                Optional<Map<String, Value>> envOpt = scope.findBinding(name);
                if (envOpt.isPresent()) {
                    return envOpt.get().get(name);
                } else {
                    throw new LanguageException("value " + name + " not function");
                }

            }
            case IfExpression ifExpression -> {
                var ifResult = interpret(ifExpression.condition(), scope);
                if (ifResult instanceof Bool) {
                    if (((Bool) ifResult).value()) {
                        return interpret(ifExpression.thenClause(), scope);
                    } else {
                        var elseConditionOpt = ifExpression.elseClause();
                        return elseConditionOpt.map(value -> interpret(value, scope)).orElse(null);
                    }
                } else {
                    throw new LanguageException("if condition not bool.");
                }
            }
            default -> {
                new RuntimeException("must not reach here");
            }
        }
        throw new RuntimeException("must not reach here");
    }

    public Value callMain(Program program) {
        Map<String, Value> builtInEnv = builtinEnvironment();
        Map<String, Value> moduleEnv = new HashMap<>();
        Scope scope = new Scope(builtInEnv, moduleEnv, new ArrayList<>());
        Map<String, Value> objectEnv = new HashMap<>();
        scope.addFuncEnv(objectEnv);


        var topLevels = program.definitions();
        topLevels.stream().filter(topLevel -> topLevel instanceof FunctionDefinition).forEach(f -> {
            FunctionDefinition functionDefinition = (FunctionDefinition) f;
            moduleEnv.put(functionDefinition.name(), FunctionValue.of(functionDefinition));
        });
        topLevels.stream().filter(topLevel -> topLevel instanceof Assignment).forEach(a -> {
            Assignment assignment = (Assignment) a;
            interpret(assignment.ast(), scope);
            moduleEnv.put(assignment.name(), interpret(assignment.ast(), scope));
        });

        return interpret(new FunctionCall("main", List.of()), scope);
    }

    private static Map<String, Value> builtinEnvironment() {
        Map<String, Value> builtinEnvironment = new HashMap<>();

        builtinEnvironment.put("print", Value.nativeFunctionValue("print", a -> {
            System.out.println(a.getFirst());
            return a.getFirst();
        }));
        return builtinEnvironment;
    }
}
