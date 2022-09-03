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

import java.util.Iterator;

public class JsonStreamParser implements Iterator<JsonToken> {
    private final char[] data;
    private final String str;
    private int loc;
    private JsonSymbol excepted;

    public JsonStreamParser(String data) {
        this.data = data.toCharArray();
        str = data;
    }

    @Override
    public boolean hasNext() {
        return loc < data.length;
    }

    @Override
    public JsonToken next() {
        int i;
        var collector = new StringBuilder();
        excepted = null;
        boolean escape = false;
        //var tokens = new ArrayList<JsonToken>();

        int objectDepth = 0;
        for (i = loc; i < data.length; i++) {
            var now = data[i];
            var hasNext = i != data.length - 1;
            var next = hasNext ? data[i + 1] : ' ';
            var hasLast = i != 0;
            var last = hasLast ? data[i - 1] : ' ';
            // read
            if (escape) {
                collector.append(now);
                escape = false;
                continue;
            } else if (now == '\\') {
                escape = true;
                continue;
            }

            switch (now) {
                case '"':
                    if (excepted == null) {
                        excepted = JsonSymbol.LITERAL_STRING;
                        continue;
                    } else if (excepted == JsonSymbol.LITERAL_STRING) { // end of an literal
                        //return new JsonToken(JsonSymbol.LITERAL_STRING,collector.toString()));
                        excepted = null;
                        loc = i + 1;
                        return new JsonToken(JsonSymbol.LITERAL_STRING, collector.toString());
                    } else {
                        throw new JsonParseException(" Excepted " + excepted + " but " + JsonTokenType.LITERAL_STRING + " is found. " + printAround(loc));
                    }

                case '{':
                    //return new JsonToken(JsonSymbol.OBJECT_BEGIN,null))/;
                    if (excepted == JsonSymbol.LITERAL_STRING) {
                        break;
                    }
                    loc = i + 1;
                    return new JsonToken(JsonSymbol.OBJECT_BEGIN, null);
                case '}':
                    if (excepted == JsonSymbol.LITERAL_STRING) {
                        break;
                    }
                    loc = i + 1;
                    return new JsonToken(JsonSymbol.OBJECT_END, null);

                case '[':
                    if (excepted == JsonSymbol.LITERAL_STRING) {
                        break;
                    }
                    loc = i + 1;
                    return new JsonToken(JsonSymbol.ARRAY_BEGIN, null);

                case ']':
                    if (excepted == JsonSymbol.LITERAL_STRING) {
                        break;
                    }
                    loc = i + 1;
                    return new JsonToken(JsonSymbol.ARRAY_END, null);
                case '\n', ' ':
                    if (excepted == JsonSymbol.LITERAL_STRING) {
                        break;
                    }
                    continue;
                case ':':
                    if (excepted == JsonSymbol.LITERAL_STRING) {
                        break;
                    }
                    loc = i + 1;
                    return new JsonToken(JsonSymbol.SEMICOLON, null);

                case ',':
                    if (excepted == JsonSymbol.LITERAL_STRING) {
                        break;
                    }
                    if (excepted == JsonSymbol.LITERAL_INT) {
                        loc = i;
                        excepted = null;
                        return new JsonToken(JsonSymbol.LITERAL_INT, collector.toString());
                    }
                    loc = i + 1;
                    return new JsonToken(JsonSymbol.COMMA, null);
            }
            //if (excepted == JsonSymbol.LITERAL_STRING || excepted == JsonS) {
            collector.append(now);
            //  continue;
            //}
            if (Character.isDigit(now) && excepted == null) {
                excepted = JsonSymbol.LITERAL_INT;
            }
        }
        loc = i + 1;
        return new JsonToken(JsonSymbol.END_OF_FILE, null);
    }

    private String printAround(int loc) {
        if (loc < 0) {
            throw new IllegalArgumentException("loc is negative");
        }
        int min;
        for (min = loc - 15; min < 0; min++) ;
        int max;
        for (max = loc + 60; max > data.length; max--) ;
        var sb = new StringBuilder();
        sb.append('\n').append(str, min, max).append('\n').append(" ".repeat(min)).append('^');
        return sb.toString();
    }
}
