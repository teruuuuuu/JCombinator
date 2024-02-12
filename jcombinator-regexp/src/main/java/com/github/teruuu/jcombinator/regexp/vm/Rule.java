package com.github.teruuu.jcombinator.regexp.vm;

import java.util.Optional;

// ParseRule型の時点で次の遷移先ルールを把握できるようにしておく
public sealed interface Rule {
    // 1文字
    record Literal(char c) implements Rule {
    }

    static Rule literal(char c) {
        return new Literal(c);
    }

    // 範囲指定文字 ex: [0-9]
    record Range(char c1, char c2) implements Rule {
    }

    static Rule range(char c1, char c2) {
        return new Range(c1, c2);
    }

    // 任意の1文字 .
    record Any() implements Rule {
    }

    static Rule any() {
        return new Any();
    }

    // 0回または1回 ex: a?
    record Option(Rule rule) implements Rule {
    }

    static Rule option(Rule rule) {
        return new Option(rule);
    }

    // 0回以上の繰り返し ex: a*
    record ZeroSeq(Rule rule) implements Rule {
    }

    static Rule zeroSeq(Rule rule) {
        return new ZeroSeq(rule);
    }

    // 1回以上の繰り返し ex: a+
    record OneSeq(Rule rule) implements Rule {
    }

    static Rule oneSeq(Rule rule) {
        return new OneSeq(rule);
    }

    // 行頭 ~
    record Head() implements Rule {
    }

    static Rule head() {
        return new Head();
    }

    // 行頭 $
    record End() implements Rule {
    }

    static Rule end() {
        return new End();
    }

    // 量指定1 ex: [0-9]{3}
    record Quantity1(Rule rule, int count) implements Rule {
    }

    static Rule quantity(Rule rule, int count) {
        return new Quantity1(rule, count);
    }

    // 量指定2 ex: [0-9]{3,5}
    record Quantity2(Rule rule, Optional<Integer> min, Optional<Integer> max) implements Rule {
    }

    static Rule quantityMore(Rule rule, int min) {
        return new Quantity2(rule, Optional.of(min), Optional.empty());
    }

    static Rule quantityLess(Rule rule, int max) {
        return new Quantity2(rule, Optional.empty(), Optional.of(max));
    }

    static Rule quantityMoreLess(Rule rule, int min, int max) {
        return new Quantity2(rule, Optional.of(min), Optional.of(max));
    }

    // 連結
    record Cons(Rule rule1, Rule rule2) implements Rule {
    }

    static Rule cons(Rule rule1, Rule rule2) {
        return new Cons(rule1, rule2);
    }

    // 選択
    record Select(Rule rule1, Rule rule2) implements Rule {
    }

    static Rule select(Rule rule1, Rule rule2) {
        return new Select(rule1, rule2);
    }

    // 否定
    record Not(Rule rule) implements Rule {
    }

    static Rule not(Rule rule) {
        return new Not(rule);
    }

    // 肯定先読み ex: (?=abc)
    record LookAhead(Rule rule) implements Rule {
    }

    static Rule lookAhead(Rule rule) {
        return new LookAhead(rule);
    }

    // 否定先読み　ex: (?!abc)
    record NotLookAhead(Rule rule) implements Rule {
    }

    static Rule notLookAhead(Rule rule) {
        return new NotLookAhead(rule);
    }

    // 肯定後よみ ex: (?<=abc)
    record LookBehind(Rule rule) implements Rule {
    }

    static Rule lookBehind(Rule rule) {
        return new LookBehind(rule);
    }

    // 否定後よみ　ex: (?<!abc)
    record NotLookBehind(Rule rule) implements Rule {
    }

    static Rule notLookBehind(Rule rule) {
        return new NotLookBehind(rule);
    }


}
