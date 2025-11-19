package by.course.zgirskaya.state;

import by.course.zgirskaya.entity.Ship;

import java.util.concurrent.TimeUnit;

public interface ShipState {
  void processUntilComplete(Ship ship);

  default void delayTask(int millisecondsCount) {
    try {
      TimeUnit.MILLISECONDS.sleep(millisecondsCount);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}