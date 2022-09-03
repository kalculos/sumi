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

import io.ib67.sumi.api.object.primitive.JsonBoolean;
import io.ib67.sumi.api.object.primitive.JsonNull;
import io.ib67.sumi.api.object.primitive.JsonNumber;
import io.ib67.sumi.api.object.primitive.JsonString;
import io.ib67.sumi.api.object.primitive.numbers.JsonDouble;
import io.ib67.sumi.api.object.primitive.numbers.JsonInt;
import io.ib67.sumi.api.object.primitive.numbers.JsonLong;

public sealed abstract class JsonPrimitive implements JsonValue permits JsonNumber, JsonNull, JsonBoolean, JsonString {
    public boolean isBoolean() {
        return this instanceof JsonBoolean;
    }

    public boolean getAsBoolean() {
        if (!(this instanceof JsonBoolean))
            throw new UnsupportedOperationException(this.getClass() + " is not a boolean");
        return this == JsonBoolean.TRUE;
    }

    public boolean isNumber() {
        return this instanceof JsonNumber;
    }

    public Number getAsNumber() {
        return ((JsonNumber) this).getNumber();
    }

    public boolean isInt() {
        return this instanceof JsonInt;
    }

    public int getAsInt() {
        return ((JsonInt) this).getValue();
    }

    public boolean isLong() {
        return this instanceof JsonLong;
    }

    public long getAsLong() {
        return ((JsonLong) this).getValue();
    }

    public boolean isDouble() {
        return this instanceof JsonDouble;
    }

    public double getAsDouble() {
        return ((JsonDouble) this).getValue();
    }

    public boolean isNull() {
        return this == JsonNull.NULL;
    }

    @Override
    public String toString() {
        return this.toJSON();
    }
}
