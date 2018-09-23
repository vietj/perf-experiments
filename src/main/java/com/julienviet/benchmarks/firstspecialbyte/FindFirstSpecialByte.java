package com.julienviet.benchmarks.firstspecialbyte;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public abstract class FindFirstSpecialByte {

  public static final Unsafe unsafe = getUnsafe();
  public static final long byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);

  private static Unsafe getUnsafe() {
    try {

      Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
      singleoneInstanceField.setAccessible(true);
      return (Unsafe) singleoneInstanceField.get(null);

    } catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public abstract long find();

  public static abstract class HeapVersion extends FindFirstSpecialByte {

    final byte[] data;
    final byte[] lookup;
    final int size;

    public HeapVersion(String data) {
      this(data.getBytes());
    }

    public HeapVersion(byte[] data) {
      this.data = data;
      this.size = data.length;
      lookup = new byte[256];
      for (int i = 0;i < 0x20;i++) {
        lookup[i] = 1;
      }
      for (int i = 128;i < 256;i++) {
        lookup[i] = 1;
      }
      lookup['"'] = 1;
      lookup['\\'] = 1;
    }
  }

  public static class Compare extends HeapVersion {

    public Compare(String data) {
      super(data);
    }

    public Compare(byte[] data) {
      super(data);
    }

    @Override
    public long find() {
      for (int i = 0;i < size;i++) {
        int b = data[i] & 0xFF;
        if (b < 0x20 || b > 127 || b == '"' || b == '\\') {
          return i;
        }
      }
      return -1;
    }
  }

  public static class Lookup extends HeapVersion {

    public Lookup(String data) {
      super(data);
    }

    public Lookup(byte[] data) {
      super(data);
    }

    @Override
    public long find() {
      for (int i = 0;i < size;i++) {
        int b = data[i] & 0xFF;
        if (lookup[b] != 0) {
          return i;
        }
      }
      return -1;
    }
  }

  public static class LookupUnrolled extends HeapVersion {

    public LookupUnrolled(String data) {
      super(data);
    }

    public LookupUnrolled(byte[] data) {
      super(data);
    }

    @Override
    public long find() {
      int i = 0;
      while (i < size) {
        int b1 = data[i] & 0xFF;
        if (lookup[b1] != 0) {
          return i;
        }
        int b2 = data[i + 1] & 0xFF;
        if (lookup[b2] != 0) {
          return i + 1;
        }
        int b3 = data[i + 2] & 0xFF;
        if (lookup[b3] != 0) {
          return i + 2;
        }
        int b4 = data[i + 3] & 0xFF;
        if (lookup[b4] != 0) {
          return i + 3;
        }
        i += 4;
      }
      return -1;
    }
  }

  public static class UnsafeCompare extends HeapVersion {

    public UnsafeCompare(String data) {
      super(data);
    }

    public UnsafeCompare(byte[] data) {
      super(data);
    }

    public long find() {
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
  }

  public static class UnsafeLookupUnrolled extends HeapVersion {

    public UnsafeLookupUnrolled(String data) {
      super(data);
    }

    public UnsafeLookupUnrolled(byte[] data) {
      super(data);
    }

    public long find() {
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
  }

  public static abstract class OffHeapBase extends FindFirstSpecialByte {

    final long offHeapData;
    final long offHeapLookup;
    final int size;

    public OffHeapBase(String data) {
      this(data.getBytes());
    }

    public OffHeapBase(byte[] data) {
      size = data.length;
      offHeapData = unsafe.allocateMemory(size);
      for (int i = 0; i < size; i++) {
        unsafe.putByte(offHeapData + i, data[i]);
      }
      offHeapLookup = unsafe.allocateMemory(256);
      for (int i = 0; i < 256;i++) {
        unsafe.putByte(offHeapLookup + i, (byte)0);
      }
      for (int i = 0;i < 0x20;i++) {
        unsafe.putByte(offHeapLookup + i, (byte)1);
      }
      for (int i = 128;i < 256;i++) {
        unsafe.putByte(offHeapLookup + i, (byte)1);
      }
      unsafe.putByte(offHeapLookup + '"', (byte)1);
      unsafe.putByte(offHeapLookup + '\\', (byte)1);
    }
  }

  public static class OffHeapCompare extends OffHeapBase {

    public OffHeapCompare(String data) {
      super(data);
    }

    public OffHeapCompare(byte[] data) {
      super(data);
    }

    public long find() {
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
  }

  public static class OffHeapLookupUnrolled extends OffHeapBase {

    public OffHeapLookupUnrolled(String data) {
      super(data);
    }

    public OffHeapLookupUnrolled(byte[] data) {
      super(data);
    }

    public long find() {
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
}
