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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.ib67.sumi.api.object.JsonValue;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@State(Scope.Benchmark)
public class JsonSerializationBenchmark {
    private static final Gson gsonSerializer = new Gson();
    private JsonNode jackson;
    private JsonElement gson;
    private JsonValue sumi;

    @Setup
    public void setup() throws IOException {
        var text = Files.readString(Path.of("test.json"));
        jackson = new ObjectMapper().readTree(text);
        gson = JsonParser.parseString(text);
        sumi = io.ib67.sumi.api.JsonParser.DEFAULT.parseString(text);
    }

    @Benchmark
    public void sumi(Blackhole hole) {
        hole.consume(sumi.toJSON());
    }

    @Benchmark
    public void jackson(Blackhole hole) {
        hole.consume(jackson.toString());
    }

    @Benchmark
    public void gson(Blackhole hole) {
        hole.consume(gsonSerializer.toJson(gson));
    }
}
