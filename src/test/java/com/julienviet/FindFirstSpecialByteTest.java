package com.julienviet;

import com.julienviet.benchmarks.FindFirstSpecialByte;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FindFirstSpecialByteTest {

  @Test
  public void testNotFound() {
    assertEquals(-1, new FindFirstSpecialByte.SimpleVersion("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.SimpleUnrolledVersion("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.UnsafeVersion("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.UnsafeUnrolledVersion("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.OffHeapVersion("ABCDEFGH").find());
    assertEquals(-1, new FindFirstSpecialByte.OffHeapUnrolledVersion("ABCDEFGH").find());
  }

  @Test
  public void testFound() {
    assertEquals(3, new FindFirstSpecialByte.SimpleVersion("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.SimpleUnrolledVersion("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.UnsafeVersion("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.UnsafeUnrolledVersion("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.OffHeapVersion("ABC\"EFGH").find());
    assertEquals(3, new FindFirstSpecialByte.OffHeapUnrolledVersion("ABC\"EFGH").find());
  }
}
