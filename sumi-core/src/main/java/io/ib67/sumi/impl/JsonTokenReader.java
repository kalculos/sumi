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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class JsonTokenReader implements Iterator<JsonToken> {
    private static final int MAX_LENGTH_OF_TEXT_WITH_ESCAPED = 1024;
    /**
     * Json input, by byte.
     */
    private final ByteBuffer input;
    private final ByteArrayOutputStream stringBuf = new ByteArrayOutputStream(MAX_LENGTH_OF_TEXT_WITH_ESCAPED);

    public JsonTokenReader(ByteBuffer input) {
        this.input = input;
    }

    private static boolean isWhitespace(byte b) {
        return b == ' ' || b == '\n' || b == '\r';
    }

    public JsonToken poll() {
        nextNonWhitespace();
        if (!input.hasRemaining()) {
            // EOF
            return JsonToken.EOF;
        }
        input.mark();
        var it = input.get();
        return switch (it) {
            case Constants.OBJECT_BEGIN -> JsonToken.OBJ_BEGN;
            case Constants.OBJECT_END -> JsonToken.OBJ_END;
            case Constants.ARRAY_BEGIN -> JsonToken.ARR_BEGN;
            case Constants.ARRAY_END -> JsonToken.ARR_END;
            case Constants.COMMA -> JsonToken.COMMA;
            case Constants.SEMICOLON -> JsonToken.SEMICOLON;
            case Constants.DOUBLE_QUOTE -> readString();
            case (byte) 't' -> readTrue();
            case (byte) 'f' -> readFalse();
            default -> {
                // check for number.
                if ((it >= 48 && it <= 57) || it == (byte) '-') {
                    yield readNumber();
                }
                throw new JsonParseException("Unexpected char: \"" + (char) it + "\"");
            }
        };
    }

    private JsonToken readNumber() {
        // move back
        input.position(input.position() - 1);
        // read until any end
        var pos = input.position();
        boolean digit = false;
        while (input.hasRemaining()) {
            var now = input.get();
            switch (now) {
                case (byte) 'e', (byte) 'E', (byte) '.' -> digit = true;
                case Constants.COMMA, Constants.OBJECT_END, Constants.ARRAY_END -> {
                    var end = input.position() - 1;
                    var dst = new byte[end - pos];
                    input.position(pos);
                    input.get(dst, 0, end - pos);
                    input.position(end);
                    return new JsonToken(digit ? TokenType.LITERAL_DOUBLE : TokenType.LITERAL_INTEGER, new String(dst));
                }
            }
        }
        throw new JsonParseException("Unexcepted EOF when reading number.");
    }

    private JsonToken readTrue() {
        var result = matchToken("rue", false); // 't' is already polled;
        if (!result) {
            throw new JsonParseException("Cannot match token \"true\"!");
        }
        return JsonToken.TRUE;
    }

    // boilerplate.
    private JsonToken readFalse() {
        var result = matchToken("alse", false); // 'f' is already polled;
        if (!result) {
            throw new JsonParseException("Cannot match token \"false\"!");
        }
        return JsonToken.FALSE;
    }

    private boolean matchToken(String token, boolean reset) {
        var bts = token.getBytes();
        if (input.remaining() < bts.length) {
            throw new JsonParseException("Cannot match token \"" + token + "\" because the buffer is going to end");
        }
        input.mark();
        for (byte n : bts) {
            if (input.get() != n) {
                if (reset) {
                    input.reset();
                }
                return false;
            }
        }
        if (reset) {
            input.reset();
        }
        return true;
    }

    private JsonToken readString() {
        var pos = input.position();
        boolean usingStringBuf = false;
        while (input.hasRemaining()) {
            var c = input.get();
            if (c == Constants.ESCAPE) {
                if (!usingStringBuf) {
                    stringBuf.reset();
                    // turn to mode 2 !
                    var current = input.position() - 1;
                    var len = input.position() - pos;
                    var buf = new byte[len];
                    input.get(buf, 0, len);
                    input.position(current);
                    //baos = new ByteArrayOutputStream(Math.max(32, len + 16));
                    stringBuf.writeBytes(buf);
                    usingStringBuf = true;
                }
                // write escaping chars.
                if (!input.hasRemaining()) {
                    throw new JsonParseException("Unexcepted EOF at len " + input.position() + ", literal or something is excepted.");
                }
                c = getEscapedChar();
            } else if (c == Constants.DOUBLE_QUOTE) {
                // end.
                if (!usingStringBuf) {
                    var current = input.position() - 1;
                    if (input.hasArray()) {
                        return new JsonToken(TokenType.LITERAL_TEXT, new String(input.array(), pos, current - pos));
                    } else {
                        var buf = new byte[current - pos];
                        input.get(buf, 0, current - pos);
                        return new JsonToken(TokenType.LITERAL_TEXT, new String(buf));
                    }
                } else {
                    return new JsonToken(TokenType.LITERAL_TEXT, stringBuf.toString());
                }
            }

            if (usingStringBuf) { // mode 2
                stringBuf.write(c);
            } // or continue.
        }
        throw new JsonParseException("Unexcepted EOF at len " + input.position() + ", a closing double-quoting is excepted.");
    }

    private byte getEscapedChar() {
        return (byte) switch ((char) input.get()) {
            case '\\' -> Constants.ESCAPE;
            case 'n' -> '\n';
            case 'b' -> '\b';
            case 't' -> '\t';
            case 'r' -> '\r';
            case 'f' -> '\f';
            case '"' -> '"';
            default -> throw new IllegalStateException("Unexpected value: " + (char) input.get());
        };
    }

    private void nextNonWhitespace() {
        while (input.hasRemaining()) { // 0 , 1 , 2
            // { , } , \n
            var a = input.get();
            if (!isWhitespace(a)) {
                input.position(input.position() - 1);
                return;
            }
            if (!input.hasRemaining()) {
                // this is end.
                return;
            }
        }
        input.position(input.position() - 1);
    }

    @Override
    public boolean hasNext() {
        return input.hasRemaining();
    }

    @Override
    public JsonToken next() {
        return poll();
    }
}
