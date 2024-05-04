package com.github.teruuu.jcombinator.regexp.vm;

import com.github.teruuu.jcombinator.core.parser.ParseContext;
import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.type.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegExp {

    private final Rule rule;

    public static Optional<RegExp> of(String regExp) {
        Parser<Rule> parser = new RegExpParser();
        Tuple<ParseContext, ParseResult<Rule>> parseResultState = parser.parse(regExp);
        ParseContext parseContext = parseResultState._1();
        ParseResult<Rule> parseResult = parseResultState._2();

        switch (parseResult) {
            case ParseResult.Success<Rule> success -> {
                return Optional.of(of(success.value()));
            }
            case ParseResult.Failure<Rule> failure -> {
                return Optional.empty();
            }
        }
    }

    public static RegExp of(Rule rule) {
        return new RegExp(rule);
    }

    public RegExp(Rule rule) {
        this.rule = rule;
    }

    public RegExpResult search(String input) {
        return search(input, 0);
    }

    public RegExpResult search(String input, int location) {
        int currentLocation = location;
        List<Location> locations = new ArrayList<>();
        while (input.length() >= currentLocation) {
            switch (search(rule, input, new Location(currentLocation, currentLocation))) {
                case RegExpResult.Success success -> {
                    locations.add(success.location());
                    if (currentLocation == success.location().to()) {
                        currentLocation++;
                    } else {
                        currentLocation = success.location().to();
                    }
                }
                case RegExpResult.Failed failed -> {
                    currentLocation++;
                }
            }
        }
        if (!locations.isEmpty()) {
            return RegExpResult.success(locations);
        } else {
            return RegExpResult.failed();
        }
    }

    private RegExpResult search(Rule rule, String input, Location location) {
        switch (rule) {
            case Rule.Literal literal -> {
                if (input.length() > location.to() && input.charAt(location.to()) == literal.c()) {
                    return new RegExpResult.Success(List.of(location.move(1)));
                } else {
                    return new RegExpResult.Failed();
                }
            }
            case Rule.Range range -> {
                if (input.length() > location.to() &&
                        input.charAt(location.to()) >= range.c1() && input.charAt(location.to()) <= range.c2()) {
                    return new RegExpResult.Success(List.of(location.move(1)));
                } else {
                    return new RegExpResult.Failed();
                }
            }
            case Rule.Any any -> {
                if (input.length() > location.to()) {
                    return new RegExpResult.Success(List.of(location.move(1)));
                } else {
                    return new RegExpResult.Failed();
                }
            }
            case Rule.Option option -> {
                switch (search(option.rule(), input, location)) {
                    case RegExpResult.Success success -> {
                        return success;
                    }
                    case RegExpResult.Failed failed -> {
                        return new RegExpResult.Success(List.of(location));
                    }
                }
            }
            case Rule.ZeroSeq zeroSeq -> {
                boolean loop = true;
                Location currentLocation = location;
                List<Location> locations = new ArrayList<>();
                locations.add(location);
                while (loop) {
                    switch (search(zeroSeq.rule(), input, currentLocation)) {
                        case RegExpResult.Success success -> {
                            currentLocation = success.location();
                            locations.add(currentLocation);
                        }
                        case RegExpResult.Failed failed -> {
                            loop = false;
                        }
                    }
                }
                return new RegExpResult.Success(locations);
            }
            case Rule.OneSeq oneSeq -> {
                boolean loop = true;
                Location currentLocation = location;
                List<Location> locations = new ArrayList<>();
                while (loop) {
                    switch (search(oneSeq.rule(), input, currentLocation)) {
                        case RegExpResult.Success success -> {
                            currentLocation = success.location();
                            locations.add(currentLocation);
                        }
                        case RegExpResult.Failed failed -> {
                            loop = false;
                        }
                    }
                }
                if (!locations.isEmpty()) {
                    return new RegExpResult.Success(locations);
                } else {
                    return new RegExpResult.Failed();
                }
            }
            case Rule.Head head -> {
                if (location.to() == 0) {
                    return new RegExpResult.Success(List.of(location));
                } else {
                    return new RegExpResult.Failed();
                }
            }
            case Rule.End end -> {
                if (input.length() == location.to()) {
                    return new RegExpResult.Success(List.of(location));
                } else {
                    return new RegExpResult.Failed();
                }
            }
            case Rule.Quantity1 quantity1 -> {
                int count = 0;
                boolean loop = true;
                Location currentLocation = location;
                while (loop) {
                    switch (search(quantity1.rule(), input, currentLocation)) {
                        case RegExpResult.Success success -> {
                            currentLocation = success.location();
                            count++;
                            if (count == quantity1.count()) {
                                loop = false;
                            }
                        }
                        case RegExpResult.Failed failed -> {
                            loop = false;
                        }
                    }
                }
                if (count == quantity1.count()) {
                    return new RegExpResult.Success(List.of(currentLocation));
                } else {
                    return new RegExpResult.Failed();
                }
            }
            case Rule.Quantity2 quantity2 -> {
                int count = 0;
                int min = quantity2.min().orElse(-1);
                int max = quantity2.max().orElse(-1);
                boolean loop = true;
                Location currentLocation = location;
                List<Location> locations = new ArrayList<>();
                while (loop) {
                    switch (search(quantity2.rule(), input, currentLocation)) {
                        case RegExpResult.Success success -> {
                            currentLocation = success.location();
                            count++;
                            if (min == -1 || count >= min) {
                                locations.add(currentLocation);
                            }
                            if (max > 0 && max == count) {
                                loop = false;
                            }
                        }
                        case RegExpResult.Failed failed -> {
                            loop = false;
                        }
                    }
                }
                if ((min == -1 || count >= min) && (max == -1 || count <= max)) {
                    return RegExpResult.success(Location.of(location.from(), currentLocation.to()));
                } else {
                    return new RegExpResult.Failed();
                }
            }
            case Rule.Cons cons -> {
                switch (search(cons.rule1(), input, location)) {
                    case RegExpResult.Success success1 -> {
                        RegExpResult regExpResult;
                        for (Location currentLocation : success1.locations().reversed()) {
                            regExpResult = search(cons.rule2(), input, currentLocation);
                            if (regExpResult instanceof RegExpResult.Success success) {
                                return success;
                            }
                        }
                        return new RegExpResult.Failed();
                    }
                    case RegExpResult.Failed failed -> {
                        return new RegExpResult.Failed();
                    }
                }
            }
            case Rule.Select select -> {
                switch (search(select.rule1(), input, location)) {
                    case RegExpResult.Success success -> {
                        return success;
                    }
                    case RegExpResult.Failed failed -> {
                        return search(select.rule2(), input, location);
                    }
                }
            }
            case Rule.Not not -> {
                switch (search(not.rule(), input, location)) {
                    case RegExpResult.Success success -> {
                        return new RegExpResult.Failed();
                    }
                    case RegExpResult.Failed failed -> {
                        if (input.length() > location.to()) {
                            return new RegExpResult.Success(List.of(location.move(1)));
                        } else {
                            return new RegExpResult.Failed();
                        }
                    }
                }
            }
            case Rule.LookAhead lookAhead -> {
                switch (search(lookAhead.rule(), input, location)) {
                    case RegExpResult.Success success -> {
                        return new RegExpResult.Success(List.of(location));
                    }
                    case RegExpResult.Failed failed -> {
                        return new RegExpResult.Failed();
                    }
                }
            }
            case Rule.NotLookAhead notLookAhead -> {
                switch (search(notLookAhead.rule(), input, location)) {
                    case RegExpResult.Success success -> {
                        return new RegExpResult.Failed();
                    }
                    case RegExpResult.Failed failed -> {
                        return new RegExpResult.Success(List.of(location));
                    }
                }
            }
            case Rule.LookBehind lookBehind -> {
                Location currentLocation = Location.of(location.to(), location.to());
                RegExpResult regExpResult;
                while (location.from() <= currentLocation.from()) {
                    regExpResult = search(lookBehind.rule(), input, currentLocation);
                    if (regExpResult instanceof RegExpResult.Success success) {
                        if (success.location().to() == location.to()) {
                            return RegExpResult.success(location);
                        }
                    }
                    currentLocation = Location.of(currentLocation.to() - 1, currentLocation.to() - 1);
                }
                return RegExpResult.failed();
            }
            case Rule.NotLookBehind notLookBehind -> {
                Location currentLocation = Location.of(location.to(), location.to());
                RegExpResult regExpResult;
                while (location.from() <= currentLocation.from()) {
                    regExpResult = search(notLookBehind.rule(), input, currentLocation);
                    if (regExpResult instanceof RegExpResult.Failed failed) {
                        return RegExpResult.success(location);
                    }
                    currentLocation = Location.of(currentLocation.from() - 1, currentLocation.to());
                }
                return RegExpResult.failed();
            }
        }
    }

}
