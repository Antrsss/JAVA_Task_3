package by.course.zgirskaya;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

class WalkThread extends Thread {
  @Override
  public void run() {
    try {
      for (int i = 0; i < 7; i++) {
        System.out.println("Walk " + i);
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } finally {
      System.out.println("Walk" + Thread.currentThread().getName());
    }
  }
}

class TalkThread implements Runnable {
  @Override
  public void run() {
    try {
      for (int i = 0; i < 7; i++) {
        System.out.println("Talk -->" + i);
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } finally {
      System.out.println("Talk" + Thread.currentThread().getName());
    }
  }
}

class ActionCallable implements Callable<Integer> {
  private List<Integer> integers;
  public ActionCallable(List<Integer> integers) {
    this.integers = integers;
  }
  @Override
  public Integer call() {
    int sum = 0;
    for (int number : integers) {
      sum += number;
    }
    return sum;
  }
}

class SumRecursiveTask extends RecursiveTask<Long> {
  private List<Long> longList;
  private int begin;
  private int end;
  public static final long THRESHOLD = 10_000;
  public SumRecursiveTask(List<Long> longList) {
    this(longList, 0, longList.size());
  }
  private SumRecursiveTask(List<Long> longList, int begin, int end) {
    this.longList = longList;
    this.begin = begin;
    this.end = end;
  }
  @Override
  protected Long compute() {
    int length = end - begin;
    long result = 0;
    if (length <= THRESHOLD) {
      for (int i = begin; i < end; i++) {
        result += longList.get(i);
      }
    } else {
      int middle = begin + length / 2;
      SumRecursiveTask taskLeft = new SumRecursiveTask(longList, begin, middle);
      taskLeft.fork(); // run async
      SumRecursiveTask taskRight = new SumRecursiveTask(longList, middle, end);
      taskRight.fork();//or compute()
      Long leftSum = taskLeft.join();
      Long rightSum = taskRight.join();
      result = leftSum + rightSum;
    }
    return result;
  }
}

public class Main {
  public static void main(String[] args) {
    WalkThread walk = new WalkThread(); // new thread object
    Thread talk = new Thread(new TalkThread()); // new thread object
    talk.start(); // start of thread
    walk.start(); // start of thread
    // TalkThread t = new TalkThread(); just an object, not a thread
    // t.run(); or talk.run();
    // method will execute, but thread will not start!

    List<Integer> list = IntStream.range(0, 1_000)
            .boxed()
            .collect(Collectors.toList());
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Integer> future = executor.submit(new ActionCallable(list));
    executor.shutdown();// stops service but not thread
// executor.submit(new Thread());/* attempt to start will throw an exception */
// executor.shutdownNow(); // stops service and all running threads
    try {
      System.out.println(future.get());
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    int end = 1_000_000;
    List<Long> numbers = LongStream.range(0, end)
            .boxed()
            .collect(Collectors.toList());
    ForkJoinTask<Long> task = new SumRecursiveTask(numbers);
    long result = new ForkJoinPool().invoke(task);
    System.out.println(result);
  }
}