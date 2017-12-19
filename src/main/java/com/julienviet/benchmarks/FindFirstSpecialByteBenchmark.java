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

@State(Scope.Benchmark)
@Threads(1)
public class FindFirstSpecialByteBenchmark extends BenchmarkBase {

  @Param({"256", "4096", "65536"})
  int size;

  FindFirstSpecialByte findSimple;
  FindFirstSpecialByte findSimpleUnrolled;
  FindFirstSpecialByte findUnsafe;
  FindFirstSpecialByte findUnsafeUnrolled;
  FindFirstSpecialByte findOffHeap;
  FindFirstSpecialByte findOffHeapUnrolled;

  @Setup
  public void setup() {
    byte[] data = new byte[size];
    for (int i = 0;i < size;i++) {
      byte val = (byte) (65 + (i % 26));
      data[i] = val;
    }
    findSimple = new FindFirstSpecialByte.SimpleVersion(data);
    findSimpleUnrolled = new FindFirstSpecialByte.SimpleUnrolledVersion(data);
    findUnsafe = new FindFirstSpecialByte.UnsafeVersion(data);
    findUnsafeUnrolled = new FindFirstSpecialByte.UnsafeUnrolledVersion(data);
    findOffHeap = new FindFirstSpecialByte.OffHeapVersion(data);
    findOffHeapUnrolled = new FindFirstSpecialByte.OffHeapUnrolledVersion(data);
  }

  @Benchmark
  public long comparison() {
    return findSimple.find();
  }

  @Benchmark
  public long comparisonUnrolled() {
    return findSimpleUnrolled.find();
  }

  @Benchmark
  public long unsafeComparison() {
    return findUnsafe.find();
  }

  @Benchmark
  public long unsafeComparisonUnrolled() {
    return findUnsafeUnrolled.find();
  }

  @Benchmark
  public long offHeapComparison() {
    return findOffHeap.find();
  }

  @Benchmark
  public long offHeapComparisonUnrolled() {
    return findOffHeapUnrolled.find();
  }
}
