package com.github.teruuu.parser.regexp;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;

import java.util.ArrayList;
import java.util.List;

public interface RegexpParser {

    ParseResult<RegexpResult> parse(String input, int location);

    default List<RegexpResult> parseRegexp(String input, int location) {
        List<RegexpResult> result = new ArrayList<>();
        int length = input.length();
        while (true) {
            if (location == length) {
                break;
            } else {
                ParseResult<RegexpResult> parseResult = this.parse(input, location);
                if (parseResult instanceof ParseResult.Success<RegexpResult> success) {
                    if (success.next() == location || success.value().text().isEmpty()) {
                        break;
                    } else {
                        result.add(success.value());
                        location = success.next();
                    }
                } else {
                    location++;
                }
            }
        }
        return result;
    }


    // ^
    Parser<RegexpResult> start = (input, location) -> {
        if (location == 0) {
            return new ParseResult.Success<>(new RegexpResult("", location, location), location);
        } else {
            return new ParseResult.Failure<>("", location);
        }
    };

    // $
    Parser<RegexpResult> end = (input, location) -> {
        if (location == input.length()) {
            return new ParseResult.Success<>(new RegexpResult("", location, location), location);
        } else {
            return new ParseResult.Failure<>("", location);
        }
    };
}
