package com.julienviet.benchmarks;

public class Main {

  public static void main(String[] args) {

    StringBuilder sb = new StringBuilder();
    for (int i = 0;i < 65536;i++) {
      sb.append('A' + (i % 26));
    }

    FindFirstSpecialByte test1 = new FindFirstSpecialByte.Compare(sb.toString());
    long t1 = 0;
    for (int i = 0;i < 100000;i++) {
      long l = test1.find();
      t1 = Math.max(t1, l);
    }

    FindFirstSpecialByte test2 = new FindFirstSpecialByte.LookupUnrolled(sb.toString());
    long t2 = 0;
    for (int i = 0;i < 100000;i++) {
      long l = test2.find();
      t2 = Math.max(t2, l);
    }

    System.out.println("t = " + t1 + t2);

  }


}
