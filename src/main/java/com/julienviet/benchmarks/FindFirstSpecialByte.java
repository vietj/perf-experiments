package com.julienviet.benchmarks;

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

  public static class SimpleVersion extends HeapVersion {

    public SimpleVersion(String data) {
      super(data);
    }

    public SimpleVersion(byte[] data) {
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

  public static class SimpleUnrolledVersion extends HeapVersion {

    public SimpleUnrolledVersion(String data) {
      super(data);
    }

    public SimpleUnrolledVersion(byte[] data) {
      super(data);
    }

    @Override
    public long find() {
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
  }

  public static class UnsafeVersion extends HeapVersion {

    public UnsafeVersion(String data) {
      super(data);
    }

    public UnsafeVersion(byte[] data) {
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

  public static class UnsafeUnrolledVersion extends HeapVersion {

    public UnsafeUnrolledVersion(String data) {
      super(data);
    }

    public UnsafeUnrolledVersion(byte[] data) {
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

  public static class OffHeapVersion extends OffHeapBase {

    public OffHeapVersion(String data) {
      super(data);
    }

    public OffHeapVersion(byte[] data) {
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

  public static class OffHeapUnrolledVersion extends OffHeapBase {

    public OffHeapUnrolledVersion(String data) {
      super(data);
    }

    public OffHeapUnrolledVersion(byte[] data) {
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
