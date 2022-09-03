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
import io.ib67.sumi.api.object.primitive.JsonString;
import io.ib67.sumi.api.object.primitive.numbers.JsonDouble;
import io.ib67.sumi.api.object.primitive.numbers.JsonInt;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class JsonReader {
    private final Iterator<JsonToken> tokenIterator;
    private List<JsonValue> stack;
    private JsonToken last1;
    private JsonToken last2;
    private JsonValue current;

    public JsonReader(Iterator<JsonToken> tokenIterator) {
        this.tokenIterator = tokenIterator;
    }

    // reduce.
    public JsonValue readJson() {
        final var it = tokenIterator;
        stack = new ArrayList<>();
        last1 = null;
        last2 = null;
        JsonValue lastRemoved = null;
        while (it.hasNext()) {
            var token = it.next();
            current = stack.isEmpty() ? null : stack.get(stack.size() - 1);
            switch (token.type()) {
                case OBJECT_BEGIN -> {
                    if (current == null) {
                        // this is a object.
                        stack.add(new JsonObject());
                    } else if (current.isJsonObject()) {
                        var newObj = new JsonObject();
                        matchPropertyAndSet(newObj, token.type());
                        stack.add(newObj);
                    } else if (current.isJsonArray()) {
                        var newObj = new JsonObject();
                        stack.add(newObj);
                        current.getAsJsonArray().add(newObj);
                    } else {
                        throw new JsonParseException("Unexcepted");
                    }
                }
                case OBJECT_END -> {
                    if (current == null || !current.isJsonObject()) {
                        throw new JsonParseException("Unexcepted OBJECT_END");
                    }
                    lastRemoved = stack.remove(stack.size() - 1);
                }
                case LITERAL_INT -> {
                    if (current == null) {
                        return parseInt(token.data());
                    }
                    if (current.isJsonObject()) {
                        matchPropertyAndSet(parseInt(token.data()), token.type());
                    } else if (current.isJsonArray()) {
                        current.getAsJsonArray().add(parseInt(token.data()));
                    } else {
                        throw new JsonParseException("Unexcepted");
                    }
                }
                case LITERAL_STRING -> {
                    if (current == null) {
                        return new JsonString((String) token.data());
                    }
                    if (current.isJsonArray()) {
                        current.getAsJsonArray().add(new JsonString((String) token.data()));
                    } else if (current.isJsonObject()) {
                        if (last1.type() == JsonSymbol.COMMA || last1.type() == JsonSymbol.OBJECT_BEGIN) {
                            // this is a key, ignore it.
                            popLast(token);
                            continue;
                        }
                        matchPropertyAndSet(new JsonString((String) token.data()), token.type());
                    } else {
                        throw new JsonParseException("Unexcepted");
                    }
                }
                case ARRAY_BEGIN -> {
                    if (current == null) {
                        stack.add(new JsonArray());
                        break;
                    }
                    if (current.isJsonArray()) {
                        var newObj = new JsonArray();
                        current.getAsJsonArray().add(newObj);
                        stack.add(newObj);
                    } else if (current.isJsonObject()) {
                        var newObj = new JsonArray();
                        matchPropertyAndSet(newObj, token.type());
                        stack.add(newObj);
                    }
                }
                case ARRAY_END -> {
                    if (current == null || !current.isJsonArray()) {
                        throw new JsonParseException("Unexcepted ARRAY_END");
                    }
                    lastRemoved = stack.remove(stack.size() - 1);
                }
            }
            popLast(token);
        }
        if (stack.size() != 0) {
            throw new JsonParseException("There're some unclosed Json elements.");
        }
        return lastRemoved;
    }

    private void popLast(JsonToken token) {
        last2 = last1;
        last1 = token;
    }

    private void matchPropertyAndSet(@NotNull JsonValue value, JsonSymbol encountered) {
        requireNonNull(last1);
        requireNonNull(last2);
        if (last1.type() == JsonSymbol.SEMICOLON && last2.type() == JsonSymbol.LITERAL_STRING) {
            current.getAsJsonObject().addProperty((String) last2.data(), value);
        } else {
            throw new JsonParseException("Unexcepted " + encountered + ", excepted: literal_string");
        }
    }

    private JsonValue parseInt(Object data) {
        var raw = (String) data;
        if (raw.contains(".") && (raw.contains("e") || raw.contains("E"))) {
            return new JsonInt(Integer.parseInt(raw));
        }
        return new JsonDouble(Double.parseDouble(raw));
    }
}
