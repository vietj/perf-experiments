/*
 * Copyright (C) 2017 Julien Viet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.julienviet.benchmarks;

import org.openjdk.jmh.annotations.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@State(Scope.Benchmark)
@Threads(1)
public class FindFirstSpecialByteBenchmark extends BenchmarkBase {

  private static Unsafe getUnsafe() {
    try {

      Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
      singleoneInstanceField.setAccessible(true);
      return (Unsafe) singleoneInstanceField.get(null);

    } catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static final Unsafe unsafe = getUnsafe();
  private static final long byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);

  @Param({"256", "4096", "65536"})
  int size;

  byte[] data;
  byte[] lookup;
  long offHeapData;
  long offHeapLookup;

  @Setup
  public void setup() {
    data = new byte[size];
    offHeapData = unsafe.allocateMemory(size);
    for (int i = 0;i < size;i++) {
      byte val = (byte) (65 + (i % 26));
      data[i] = val;
      unsafe.putByte(offHeapData + i, val);
    }
    lookup = new byte[256];
    offHeapLookup = unsafe.allocateMemory(256);
    for (int i = 0;i < 0x20;i++) {
      lookup[i] = 1;
      unsafe.putByte(offHeapLookup + i, (byte)1);
    }
    for (int i = 128;i < 256;i++) {
      lookup[i] = 1;
      unsafe.putByte(offHeapLookup + i, (byte)1);
    }
    lookup['"'] = 1;
    unsafe.putByte(offHeapLookup + '"', (byte)1);
    lookup['\\'] = 1;
    unsafe.putByte(offHeapLookup + '\\', (byte)1);
  }

  @Benchmark
  public long comparison() {
    for (int i = 0;i < size;i++) {
      int b = data[i] & 0xFF;
      if (b < 0x20 || b > 127 || b == '"' || b == '\\') {
        return i;
      }
    }
    return -1;
  }

  @Benchmark
  public long comparisonUnrolled() {
    int i = 0;
    while (i < size) {
      int b1 = data[i] & 0xFF;
      if (b1 < 0x20 || b1 > 127 || b1 == '"' || b1 == '\\') {
        return i;
      }
      int b2 = data[i + 1] & 0xFF;
      if (b2 < 0x20 || b2 > 127 || b2 == '"' || b2 == '\\') {
        return i + 1;
      }
      int b3 = data[i + 2] & 0xFF;
      if (b3 < 0x20 || b3 > 127 || b3 == '"' || b3 == '\\') {
        return i + 2;
      }
      int b4 = data[i + 3] & 0xFF;
      if (b4 < 0x20 || b4 > 127 || b4 == '"' || b4 == '\\') {
        return i + 3;
      }
      i += 4;
    }
    return -1;
  }

  @Benchmark
  public long unsafeComparison() {
    long addr = byteArrayOffset;
    long limit = addr + size;
    while (addr < limit) {
      int b = unsafe.getByte(data, addr) & 0xFF;
      if (b < 0x20 || b > 127 || b == '"' || b == '\\') {
        return addr - byteArrayOffset;
      }
      addr++;
    }
    return -1;
  }

  @Benchmark
  public long unsafeComparisonUnrolled() {
    long addr = byteArrayOffset;
    long limit = addr + size;
    while (addr < limit) {
      int b1 = unsafe.getByte(data, addr) & 0xFF;
      if (unsafe.getByte(lookup, byteArrayOffset + b1) != 0) {
        return addr - byteArrayOffset;
      }
      int b2 = unsafe.getByte(data, addr + 1) & 0xFF;
      if (unsafe.getByte(lookup, byteArrayOffset + b2) != 0) {
        return addr - byteArrayOffset + 1;
      }
      int b3 = unsafe.getByte(data, addr + 2) & 0xFF;
      if (unsafe.getByte(lookup, byteArrayOffset + b3) != 0) {
        return addr - byteArrayOffset + 2;
      }
      int b4 = unsafe.getByte(data, addr + 3) & 0xFF;
      if (unsafe.getByte(lookup, byteArrayOffset + b4) != 0) {
        return addr - byteArrayOffset + 3;
      }
      addr += 4;
    }
    return -1;
  }

  @Benchmark
  public long offHeapComparison() {
    long addr = offHeapData;
    long limit = addr + size;
    while (addr < limit) {
      int b = unsafe.getByte(addr) & 0xFF;
      if (b < 0x20 || b > 127 || b == '"' || b == '\\') {
        return addr - offHeapData;
      }
      addr++;
    }
    return -1;
  }

  @Benchmark
  public long offHeapComparisonUnrolled() {
    long addr = offHeapData;
    long limit = addr + size;
    while (addr < limit) {
      int b1 = unsafe.getByte(addr) & 0xFF;
      if (unsafe.getByte(offHeapLookup + b1) != 0) {
        return addr - offHeapData;
      }
      int b2 = unsafe.getByte( addr + 1) & 0xFF;
      if (unsafe.getByte(offHeapLookup + b2) != 0) {
        return addr - offHeapData + 1;
      }
      int b3 = unsafe.getByte( addr + 2) & 0xFF;
      if (unsafe.getByte(offHeapLookup + b3) != 0) {
        return addr - offHeapData + 2;
      }
      int b4 = unsafe.getByte( addr + 3) & 0xFF;
      if (unsafe.getByte(offHeapLookup + b4) != 0) {
        return addr - offHeapData + 3;
      }
      addr += 4;
    }
    return -1;
  }
}
