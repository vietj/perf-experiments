package com.julienviet;

import com.julienviet.benchmarks.firstspecialbyte.FindFirstSpecialByte;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FindFirstSpecialByteTest {

  @Test
  public void testNotFound() {
    assertEquals(-1, new FindFirstSpecialByte.Compare("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.LookupUnrolled("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.UnsafeCompare("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.UnsafeLookupUnrolled("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.OffHeapCompare("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.OffHeapLookupUnrolled("ABCDEFGH").find());
  }

  @Test
  public void testFound() {
    assertEquals(3, new FindFirstSpecialByte.Compare("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.LookupUnrolled("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.UnsafeCompare("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.UnsafeLookupUnrolled("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.OffHeapCompare("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.OffHeapLookupUnrolled("ABC\"EFGH").find());
  }
}
