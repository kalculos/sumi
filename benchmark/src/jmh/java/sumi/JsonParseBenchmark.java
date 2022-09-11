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

package sumi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@State(Scope.Benchmark)
public class JsonParseBenchmark {
    private static final ObjectMapper om = new ObjectMapper();
    private String text;
    private byte[] bytes;

    @Setup
    public void setup() throws IOException {
        text = Files.readString(Path.of("test.json"));
        bytes = text.getBytes();
    }

    @Benchmark
    public void GsonJsonParse(Blackhole b) {
        b.consume(com.google.gson.JsonParser.parseReader(new InputStreamReader(new ByteArrayInputStream(bytes))));
        //b.consume(com.google.gson.JsonParser.parseString(text));
    }

    @Benchmark
    public void SumiJsonParse(Blackhole b) {
        b.consume(io.ib67.sumi.api.JsonParser.DEFAULT.parseBytes(bytes));
        //b.consume(io.ib67.sumi.api.JsonParser.DEFAULT.parseString(text));
    }

    // @Benchmark
    public void JacksonJsonParse(Blackhole b) throws IOException {
        b.consume(om.readTree(bytes));
    }
}
