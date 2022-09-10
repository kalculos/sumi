/*
 * MIT License
 *
 * Copyright (c) 2022 Kalculos Hub and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.ib67.sumi.impl;

import io.ib67.sumi.api.exception.JsonParseException;
import io.ib67.sumi.api.object.JsonObject;
import io.ib67.sumi.api.object.JsonValue;
import io.ib67.sumi.api.object.primitive.JsonArray;
import io.ib67.sumi.api.object.primitive.JsonBoolean;
import io.ib67.sumi.api.object.primitive.JsonNull;
import io.ib67.sumi.api.object.primitive.JsonString;
import io.ib67.sumi.api.object.primitive.numbers.JsonDouble;
import io.ib67.sumi.api.object.primitive.numbers.JsonInt;

import java.util.Iterator;
import java.util.Objects;

public class JsonReader {
    private final Iterator<JsonToken> tokenIterator;

    public JsonReader(Iterator<JsonToken> tokenIterator) {
        Objects.requireNonNull(tokenIterator);
        this.tokenIterator = tokenIterator;
    }

    /**
     * Only call it when the OBJECT_BEGIN was polled.
     */
    public JsonObject readObject() {
        if (!tokenIterator.hasNext()) {
            throw new JsonParseException("Cannot iterate more json tokens for reading an object.");
        }
        final var obj = new JsonObject();
        while (tokenIterator.hasNext()) {
            final var token = tokenIterator.next();
            switch (token.typeOfToken()) {
                case LITERAL_TEXT -> {
                    if (!tokenIterator.hasNext()) {
                        throw new JsonParseException("Unexcepted Literal: " + token.data());
                    }
                    var subToken = tokenIterator.next();
                    if (subToken.typeOfToken() != TokenType.SEMICOLON) {
                        throw new JsonParseException("Except SEMICOLON but encounter" + subToken.typeOfToken());
                    }
                    obj.addProperty((String) token.data(), readValue());
                }
                case OBJECT_END -> {
                    return obj;
                }
            }
        }
        throw new JsonParseException("Unclosed JSON Object");
    }

    public JsonValue readValue() {
        if (tokenIterator.hasNext()) {
            final var token = tokenIterator.next();
            return readValue(token);
        } else {
            throw new JsonParseException("TokenStream is ended");
        }
    }

    private JsonValue readValue(JsonToken token) {
        return switch (token.typeOfToken()) {
            case OBJECT_BEGIN -> readObject();
            case ARRAY_BEGIN -> readArray();
            case LITERAL_TEXT -> new JsonString((String) token.data());
            case LITERAL_INTEGER -> new JsonInt(Integer.parseInt(((String) token.data()).trim()));
            case LITERAL_DOUBLE -> new JsonDouble(Double.parseDouble((String) token.data()));
            case NULL -> JsonNull.NULL;
            case TRUE -> JsonBoolean.TRUE;
            case FALSE -> JsonBoolean.FALSE;
            default -> throw new JsonParseException("Impossible token is encountered: " + token);
        };
    }

    public JsonArray readArray() {
        if (!tokenIterator.hasNext()) {
            throw new JsonParseException("Cannot iterate more json tokens for reading an array.");
        }
        final var arr = new JsonArray();
        while (tokenIterator.hasNext()) {
            final var token = tokenIterator.next();
            switch (token.typeOfToken()) {
                case ARRAY_END -> {
                    return arr;
                }
                case COMMA -> {
                }
                default -> arr.add(readValue(token));
            }
        }
        throw new JsonParseException("Unclosed JSON Array.");
    }
}
