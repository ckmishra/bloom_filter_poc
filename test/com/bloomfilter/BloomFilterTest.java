package com.bloomfilter;

import java.util.*;
import java.lang.*;
import java.lang.management.*;
import com.bloomfilter.BloomFilter;
/*
 @author cmishra

*/
public class BloomFilterTest {
  int elements =  1_000_000;
  float fp_prob  = 0.01f;
  BloomFilter filter;
  Random prng;
  ThreadMXBean bean;
  MemoryMXBean memoryMXBean;

  public BloomFilterTest() {
    System.out.format("Testing a bloom filter containing n=%d elements ", elements);
    bean = ManagementFactory.getThreadMXBean();
    memoryMXBean = ManagementFactory.getMemoryMXBean();
    prng = new Random();
    prng.setSeed(0);
    filter = new BloomFilter(elements, fp_prob);
  }

  private void captureCPUUsage() {
      ThreadInfo info = bean.getThreadInfo(Thread.currentThread().getId());
      System.out.println("Thread name: " + info.getThreadName());
      System.out.println("Thread State: " + info.getThreadState());
      System.out.println(String.format("CPU time: %s ns", bean.getCurrentThreadCpuTime()));
  }

  private  void captureMemoryUsage() {
    System.out.println(String.format("Initial memory: %.2f GB",
      (double)memoryMXBean.getHeapMemoryUsage().getInit() /1073741824));
    System.out.println(String.format("Used heap memory: %.2f GB",
      (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824));
    System.out.println(String.format("Max heap memory: %.2f GB",
      (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824));
    System.out.println(String.format("Committed memory: %.2f GB",
      (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824));
  }

  public void testCorrectness() {
    System.out.println("Testing correctness.\n Creating a Set and filling it together with our filter...");
    filter.clear();
    for(int i=0; i<elements; i++) {
      int v = prng.nextInt();
      filter.add(v);
      assert filter.contains(v) : "There should be no false negative";
    }
  }

  public void testAdd() {
    System.out.println("Testing insertion speed...");

    filter.clear();
    long start = bean.getCurrentThreadCpuTime();
    for(int i=0; i<elements; i++) filter.add(prng.nextInt());
    long end = bean.getCurrentThreadCpuTime();
    long time = end - start;
    int size = filter.getBloomFilterSize();
    System.out.format("Size (byte) of filter in memory: %d\n",size);
    System.out.format("Inserted %d elements in %d ns.\n Insertion speed: %g elements/second\n\n", elements, time, elements/(time*1e-9));
  }

  public void testContains() {
    System.out.println("Testing query speed...");

    filter.clear();
    //captureCPUUsage();
    //captureMemoryUsage();
    for(int i=0; i<elements; i++) filter.add(prng.nextInt());

    boolean xor = true; // Make sure our result isn’t optimized out
    long start = bean.getCurrentThreadCpuTime();
    for(int i=0; i<elements; i++) xor ^= filter.contains(prng.nextInt());
    long end = bean.getCurrentThreadCpuTime();
    long time = end - start;

    System.out.format(
        "Queried %d elements in %d ns.\n" +
        "Query speed: %g elements/second\n\n",
        elements,
        time,
        elements/(time*1e-9)
    );
    //captureCPUUsage();
    //captureMemoryUsage();
  }

  public static void main(String[] args) {
    BloomFilterTest bloomFilterTest = new BloomFilterTest();
    bloomFilterTest.testCorrectness();
    bloomFilterTest.testAdd();
    bloomFilterTest.testContains();
  }
}
