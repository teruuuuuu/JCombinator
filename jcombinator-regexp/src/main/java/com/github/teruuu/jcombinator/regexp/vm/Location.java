package com.github.teruuu.jcombinator.regexp.vm;

public record Location(int from, int to) {

    public static Location of(int from, int to) {
        return new Location(from, to);
    }

    public Location move(int size) {
        return new Location(from, to + size);
    }

    public Location adjust(int to) {
        return new Location(from, to);
    }
}
