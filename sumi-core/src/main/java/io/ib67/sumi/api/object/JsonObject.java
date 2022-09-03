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

package io.ib67.sumi.api.object;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class JsonObject implements JsonValue, Function<String, JsonValue> {
    private final Map<String, JsonValue> values = new HashMap<>();

    public JsonObject addProperty(String key, JsonValue value) {
        values.put(key, value);
        return this;
    }

    public JsonObject removeProperty(String key) {
        values.remove(key);
        return this;
    }

    public JsonValue getProperty(String key) {
        return values.get(key);
    }

    @Override
    public String toString() {
        return this.toJSON();
    }

    @Override
    public String toJSON() {
        return values.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\":" + entry.getValue().toJSON())
                .collect(Collectors.joining(",", "{", "}"));
    }

    public Map<String, JsonValue> getAsMap() {
        return values;
    }

    @Override
    public JsonValue apply(String s) {
        return values.get(s);
    }
}
