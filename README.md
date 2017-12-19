# Various performance experiments in Java

## Find first special byte benchmark 

Attempt to reproduce the optimization explained in this article https://chadaustin.me/2017/05/writing-a-really-really-fast-json-parser/
in Java using various memory access.

The problem at hand: given a pointer to the first byte after the opening quote of a string, find the
first special byte, where special bytes are “, \, <0x20, or >0x7f.

Build the fat jar

```
> mvn clean package
```

Run the benchmark at different sizes: 256, 4096, 65536

```
> java -jar target/benchmarks.jar FindFirstSpecialByteBenchmark
```

Or at a specific size

```
> java -jar target/benchmarks.jar -p size=256 FindFirstSpecialByteBenchmark
```
