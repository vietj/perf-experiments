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

## Perfasm

On OSX:

```
> java .... -prof dtraceasm
```

### requires hsdis

```
#!/bin/bash -e

# Download OpenJDK Reference Implementation Sources from
# http://jdk.java.net/java-se-ri/10
curl -O https://download.java.net/openjdk/jdk10/ri/openjdk-10_src.zip

# Navigate to the hsdis sources
unzip openjdk-10_src.zip
cd openjdk/src/utils/hsdis

# Download binutils 2.26
curl -O https://mirrors.syringanetworks.net/gnu/binutils/binutils-2.26.tar.gz
tar xzvf binutils-2.26.tar.gz

# Build hsdis
make BINUTILS=binutils-2.26 all64

# Install hsdis
sudo cp build/macosx-amd64/hsdis-amd64.dylib /Library/Java/JavaVirtualMachines/jdk-10.jdk/Contents/Home/lib/server
```

### disable SIP

http://osxdaily.com/2015/10/05/disable-rootless-system-integrity-protection-mac-os-x/
