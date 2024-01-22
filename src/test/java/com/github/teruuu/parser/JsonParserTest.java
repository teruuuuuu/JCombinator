package com.github.teruuu.parser;

import com.github.teruuu.parser.common.ParseResult;
import com.github.teruuu.parser.common.Parser;
import com.github.teruuu.parser.common.ParserBase;
import com.github.teruuu.parser.common.type.Either;
import com.github.teruuu.parser.common.type.Tuple;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonParserTest {

    // https://www.json.org/json-en.html
    sealed interface Json {
        record JArray(List<Json> value) implements Json {}
        record JBoolean(boolean value) implements Json {}
        record JNull() implements Json {}
        record JNumber(Either<Integer, Double> value) implements Json {}
        record JObject(Map<String, Json> value) implements Json {}
        record JString(String value) implements Json {}

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

    Parser<Json> parser = new Parser<>() {
        Parser<Json> jNumberParser = new ParserBase<>() {
            @Override
            protected Parser<Json> genParser() {
                Parser<Json> parser = Parser.number().map(Json.JNumber::new);
                return parser.withSkipSpace();
            }
        };
        Parser<Json> jStringParser = new ParserBase<>() {
            @Override
            protected Parser<Json> genParser() {
                Parser<Json> parser = Parser.dquoteString().map(Json.JString::new);
                return parser.withSkipSpace();
            }
        };

        private Parser<Json> jBooleanParser = new ParserBase<>() {
            @Override
            protected Parser<Json> genParser() {
                Parser<Json> trueParser = Parser.skip("true").pure(new Json.JBoolean(true));
                Parser<Json> falseParser = Parser.skip("false").pure(new Json.JBoolean(false));
                return trueParser.or(falseParser).withSkipSpace();
            }

        };

        private Parser<Json> jNullParser = new ParserBase<>() {
            @Override
            protected Parser<Json> genParser() {
                Parser<Json> parser = Parser.skip("null").pure(new Json.JNull());
                return parser.withSkipSpace();
            }

        };
        Parser<Json> jArrayParser = new ParserBase<>() {
            @Override
            protected Parser<Json> genParser() {
                return Parser.array(Parser.skip('[').withSkipSpace(), Parser.skip(']').withSkipSpace(), Parser.skip(',').withSkipSpace(), jsonParser).map(Json.JArray::new);
            }
        };

        Parser<Json> jObjectParser = new ParserBase<>() {
            @Override
            protected Parser<Json> genParser() {
                Parser<Tuple<String, Json>> entryParer = Parser.dquoteString().withSkipSpace().andLeft(Parser.skip(':').withSkipSpace()).and(jsonParser);
                return entryParer.array(Parser.skip('{').withSkipSpace(), Parser.skip('}').withSkipSpace(), Parser.skip(",").withSkipSpace()).map(value ->
                        new Json.JObject(value.stream().collect(Collectors.toMap(Tuple::_1, Tuple::_2))));
            }
        };

        Parser<Json> jsonParser = new ParserBase<>() {
            @Override
            protected Parser<Json> genParser() {
                return jNumberParser.or(jStringParser).or(jBooleanParser).or(jNullParser).or(jArrayParser).or(jObjectParser);
            }
        };

        @Override
        public ParseResult<Json> parse(String input, int location) {
            return jsonParser.parse(input, location);
        }
    };

    @Test
    public void test() {
        ParseResult<Json> parseResult;
        Json value;

        parseResult = parser.parse("1234567890");
        assertTrue(parseResult instanceof ParseResult.Success<Json>);
        value = ((ParseResult.Success<Json>) parseResult).value();
        assertEquals(value, new Json.JNumber(Either.left(1234567890)));

        parseResult = parser.parse("1234567890.09876544321");
        assertTrue(parseResult instanceof ParseResult.Success<Json>);
        value = ((ParseResult.Success<Json>) parseResult).value();
        assertEquals(value, new Json.JNumber(Either.right(1234567890.09876544321)));

        parseResult = parser.parse("\"abcdefghijklmn\\\"0987654321!\"");
        assertTrue(parseResult instanceof ParseResult.Success<Json>);
        value = ((ParseResult.Success<Json>) parseResult).value();
        assertEquals(value, new Json.JString("abcdefghijklmn\"0987654321!"));

        parseResult = parser.parse("  true");
        assertTrue(parseResult instanceof ParseResult.Success<Json>);
        value = ((ParseResult.Success<Json>) parseResult).value();
        assertEquals(value, new Json.JBoolean(true));

        parseResult = parser.parse("  false");
        assertTrue(parseResult instanceof ParseResult.Success<Json>);
        value = ((ParseResult.Success<Json>) parseResult).value();
        assertEquals(value, new Json.JBoolean(false));

        parseResult = parser.parse("  null");
        assertTrue(parseResult instanceof ParseResult.Success<Json>);
        value = ((ParseResult.Success<Json>) parseResult).value();
        assertEquals(value, new Json.JNull());

        parseResult = parser.parse("  [ 123, \t \"abc\", true, null, \n [456.789, { \"string\": \"aaaaa\", \"numberInt\": 123, \"numberDouble\": -123.456, \"bool\": true, \"null\": null}]]");
        assertTrue(parseResult instanceof ParseResult.Success<Json>);
        value = ((ParseResult.Success<Json>) parseResult).value();
        assertEquals(value, Json.jArray(List.of(
                Json.jNumber(Either.left(123)),
                Json.jString("abc"),
                Json.jBoolean(true),
                Json.jNull(),
                Json.jArray(List.of(
                        Json.jNumber(Either.right(456.789)),
                        Json.jObject(Map.of(
                                "string", Json.jString("aaaaa"),
                                "numberInt", Json.jNumber(Either.left(123)),
                                "numberDouble", Json.jNumber(Either.right(-123.456)),
                                "bool", Json.jBoolean(true),
                                "null", Json.jNull()
                        ))
                ))
        )));
    }
}
