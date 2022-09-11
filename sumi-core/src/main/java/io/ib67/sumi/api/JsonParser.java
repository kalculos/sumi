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

package io.ib67.sumi.api;

import io.ib67.sumi.api.object.JsonValue;
import io.ib67.sumi.impl.SumiJsonParser;

import java.nio.ByteBuffer;

public interface JsonParser {

    static JsonParser DEFAULT = new SumiJsonParser();

    JsonValue parseString(String input);

    JsonValue parseBuffer(ByteBuffer buffer);

    JsonValue parseBytes(byte[] bytes);

    <T> T fromJson(String input, Class<T> typeOfT); //todo: typetoken

    <T> T fromJsonTree(JsonValue tree, Class<T> typeOfT); //todo: typetoken

    JsonValue toJson(Object object);
}
