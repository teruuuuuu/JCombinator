package com.github.teruuu.parser.regexp;

public sealed interface Regexp {

    // ^
    record Start(Regexp regexp) implements Regexp {
    }

}
