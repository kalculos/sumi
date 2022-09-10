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

import io.ib67.sumi.api.object.primitive.JsonNull;

public record JsonToken(
        TokenType typeOfToken,
        Object data
) {
    static JsonToken OBJ_BEGN = new JsonToken(TokenType.OBJECT_BEGIN, null);
    static JsonToken OBJ_END = new JsonToken(TokenType.OBJECT_END, null);
    static JsonToken ARR_BEGN = new JsonToken(TokenType.ARRAY_BEGIN, null);
    static JsonToken ARR_END = new JsonToken(TokenType.ARRAY_END, null);
    static JsonToken NULL = new JsonToken(TokenType.NULL, JsonNull.NULL);
    static JsonToken EOF = new JsonToken(TokenType.EOF, null);

    static JsonToken COMMA = new JsonToken(TokenType.COMMA, null);
    static JsonToken SEMICOLON = new JsonToken(TokenType.SEMICOLON, null);

    static JsonToken TRUE = new JsonToken(TokenType.TRUE, null);
    static JsonToken FALSE = new JsonToken(TokenType.FALSE, null);


}
