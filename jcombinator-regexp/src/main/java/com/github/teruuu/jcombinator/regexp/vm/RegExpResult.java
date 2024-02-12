package com.github.teruuu.jcombinator.regexp.vm;

import java.util.List;

public sealed interface RegExpResult {

    record Success(List<Location> locations) implements RegExpResult {
        public Location location() {
            return locations.getLast();
        }
    }

    record Failed() implements RegExpResult {
    }

    static RegExpResult.Success success(List<Location> locations) {
        return new RegExpResult.Success(locations);
    }

    static RegExpResult.Success success(Location location) {
        return new RegExpResult.Success(List.of(location));
    }

    static RegExpResult.Success success(Location... location) {
        return new RegExpResult.Success(List.of(location));
    }

    static RegExpResult.Failed failed() {
        return new RegExpResult.Failed();
    }
}
