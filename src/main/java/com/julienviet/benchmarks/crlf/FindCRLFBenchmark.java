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
package com.julienviet.benchmarks.crlf;

import com.julienviet.benchmarks.BenchmarkBase;
import com.julienviet.benchmarks.firstspecialbyte.FindFirstSpecialByte;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@Threads(1)
public class FindCRLFBenchmark extends BenchmarkBase {

  private static final byte[] bytes = { '\r', '\n' };

  @Param({"32", "64", "128", "256"})
  int size;

  byte[] data;

  @Setup
  public void setup() {
    data = new byte[size];
    for (int i = 0;i < size;i++) {
      byte val = (byte) (65 + (i % 26));
      data[i] = val;
    }
    data[data.length - 2] = '\r';
    data[data.length - 1] = '\n';
  }

  @Benchmark
  public long v1() {
    int len = data.length;
    for (int i = 0;i < len - 1;i++) {
      int b = data[i];
      if (data[i] == '\r' && data[i + 1] == '\n') {
        return i;
      }
    }
    return -1;
  }

  @Benchmark
  public long v2() {
    int len = data.length;
    int a = data[0];
    for (int i = 1;i < len;i++) {
      int b = data[i];
      if (a == '\r' && b == '\n') {
        return i;
      }
      a = b;
    }
    return -1;
  }

  @Benchmark
  public long v3() {
    int len = data.length;
    int a = data[0];
    for (int i = 1;i < len;i++) {
      int b = data[i];
      if (a == bytes[0] && b == bytes[1]) {
        return i;
      }
      a = b;
    }
    return -1;
  }
}
