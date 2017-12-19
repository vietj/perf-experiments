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

  FindFirstSpecialByte compare;
  FindFirstSpecialByte lookup;
  FindFirstSpecialByte lookupUnrolled;
  FindFirstSpecialByte unsafeCompare;
  FindFirstSpecialByte unsafeLookupUnrolled;
  FindFirstSpecialByte offHeapCompare;
  FindFirstSpecialByte offHeapLookupUnrolled;

  @Setup
  public void setup() {
    byte[] data = new byte[size];
    for (int i = 0;i < size;i++) {
      byte val = (byte) (65 + (i % 26));
      data[i] = val;
    }
    compare = new FindFirstSpecialByte.Compare(data);
    lookup = new FindFirstSpecialByte.Lookup(data);
    lookupUnrolled = new FindFirstSpecialByte.LookupUnrolled(data);
    unsafeCompare = new FindFirstSpecialByte.UnsafeCompare(data);
    unsafeLookupUnrolled = new FindFirstSpecialByte.UnsafeLookupUnrolled(data);
    offHeapCompare = new FindFirstSpecialByte.OffHeapCompare(data);
    offHeapLookupUnrolled = new FindFirstSpecialByte.OffHeapLookupUnrolled(data);
  }

  @Benchmark
  public long compare() {
    return compare.find();
  }

  @Benchmark
  public long lookup() {
    return lookup.find();
  }

  @Benchmark
  public long lookupUnrolled() {
    return lookupUnrolled.find();
  }

  @Benchmark
  public long unsafeCompare() {
    return unsafeCompare.find();
  }

  @Benchmark
  public long unsafeLookupUnrolled() {
    return unsafeLookupUnrolled.find();
  }

  @Benchmark
  public long offHeapCompare() {
    return offHeapCompare.find();
  }

  @Benchmark
  public long offHeapLookupUnrolled() {
    return offHeapLookupUnrolled.find();
  }
}
