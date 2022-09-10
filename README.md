# Sumi

Sumi is a compile-time json (de)serializer, aiming to provide a higher performance while keeping its size low.

It is lightweight, you can feel free to embed it into your application without worrying it is bigger than your all
business codes.

Status: Under Development, PoC.

# Feature

- Java 9 Module System Support
- Lightweight (21Kb for `sumi-core`)
- Fast
- Zero dependencies

# Performance

![img](./assets/img.png)
[Json for benchmark](./benchmark/test.json)
```
CPU: AMD Ryzen 7 4800U with Radeon Graphics (16) @ 1.800GHz
Memory: 15357MiB in total
# JMH version: 1.29
# VM version: JDK 17.0.2, OpenJDK 64-Bit Server VM, 17.0.2+8-LTS
# VM invoker: /home/icybear/.jdks/azul-17.0.2/bin/java
# VM options: -Dfile.encoding=UTF-8 -Djava.io.tmpdir=/home/icybear/IdeaProjects/sumi/benchmark/build/tmp/jmh -Duser.country=US -Duser.language=en -Duser.variant
# Blackhole mode: full + dont-inline hint
# Warmup: 4 iterations, 10 s each
# Measurement: 3 iterations, 10 s each
# Timeout: 10 min per iteration


Benchmark                             Mode  Cnt    Score    Error  Units
JsonParseBenchmark.GsonJsonParse     thrpt    6  354.937 ± 27.911  ops/s
JsonParseBenchmark.JacksonJsonParse  thrpt    6  992.664 ± 73.845  ops/s
JsonParseBenchmark.SumiJsonParse     thrpt    6  484.782 ± 12.654  ops/s
```

# To-do

- [x] Json Parsing
- [ ] Compile-time serialization
- [ ] Runtime bytecode generation
- [ ] More Json Features
- [ ] Optimize code to reduce garbage