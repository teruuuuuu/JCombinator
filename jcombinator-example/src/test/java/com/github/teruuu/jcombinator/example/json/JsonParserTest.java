package com.github.teruuu.jcombinator.example.json;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.type.Either;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonParserTest {
    @Test
    public void test() {
        ParseResult<Json> parseResult;
        Json value;
        Parser<Json> parser = new JsonParser();

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
