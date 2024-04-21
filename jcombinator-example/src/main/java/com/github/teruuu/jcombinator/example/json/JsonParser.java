package com.github.teruuu.jcombinator.example.json;

import com.github.teruuu.jcombinator.core.parser.ParseResult;
import com.github.teruuu.jcombinator.core.parser.Parser;
import com.github.teruuu.jcombinator.core.parser.ParserBase;
import com.github.teruuu.jcombinator.core.parser.ParserContext;
import com.github.teruuu.jcombinator.core.parser.type.Tuple;

import java.util.stream.Collectors;

/**
 * https://www.json.org/json-en.html
 */
public class JsonParser implements Parser<Json> {
    private final Parser<Json> parser;

    public JsonParser() {
        this.parser = new Parser<>() {
            private final Parser<Json> jNumberParser = new ParserBase<>() {
                @Override
                protected Parser<Json> genParser() {
                    Parser<Json> parser = Parser.number().map(Json.JNumber::new);
                    return parser.withSkipSpace();
                }
            };
            private final Parser<Json> jStringParser = new ParserBase<>() {
                @Override
                protected Parser<Json> genParser() {
                    Parser<Json> parser = Parser.dQuoteString().map(Json.JString::new);
                    return parser.withSkipSpace();
                }
            };

            private final Parser<Json> jBooleanParser = new ParserBase<>() {
                @Override
                protected Parser<Json> genParser() {
                    Parser<Json> trueParser = Parser.skip("true").pure(new Json.JBoolean(true));
                    Parser<Json> falseParser = Parser.skip("false").pure(new Json.JBoolean(false));
                    return trueParser.or(falseParser).withSkipSpace();
                }

            };

            private final Parser<Json> jNullParser = new ParserBase<>() {
                @Override
                protected Parser<Json> genParser() {
                    Parser<Json> parser = Parser.skip("null").pure(new Json.JNull());
                    return parser.withSkipSpace();
                }

            };
            private final Parser<Json> jArrayParser = new ParserBase<>() {
                @Override
                protected Parser<Json> genParser() {
                    return Parser.array(Parser.skip('[').withSkipSpace(), Parser.skip(']').withSkipSpace(), Parser.skip(',').withSkipSpace(), jsonParser).map(Json.JArray::new);
                }
            };

            private final Parser<Json> jObjectParser = new ParserBase<>() {
                @Override
                protected Parser<Json> genParser() {
                    Parser<Tuple<String, Json>> entryParer = Parser.dQuoteString().withSkipSpace().andLeft(Parser.skip(':').withSkipSpace()).and(jsonParser);
                    return entryParer.array(Parser.skip('{').withSkipSpace(), Parser.skip('}').withSkipSpace(), Parser.skip(",").withSkipSpace()).map(value ->
                            new Json.JObject(value.stream().collect(Collectors.toMap(Tuple::_1, Tuple::_2))));
                }
            };
            private final Parser<Json> jsonParser = new ParserBase<>() {
                @Override
                protected Parser<Json> genParser() {
                    return jNumberParser.or(jStringParser).or(jBooleanParser).or(jNullParser).or(jArrayParser).or(jObjectParser);
                }
            };

            @Override
            public ParseResult<Json> parse(String input, ParserContext context) {
                return jsonParser.parse(input, context);
            }
        };
    }

    @Override
    public ParseResult<Json> parse(String input, ParserContext context) {
        return parser.parse(input, context);
    }
}
