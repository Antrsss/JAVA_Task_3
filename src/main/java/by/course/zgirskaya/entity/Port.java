package by.course.zgirskaya.entity;

import by.course.zgirskaya.state.impl.CompletedState;
import by.course.zgirskaya.state.impl.LoadingState;
import by.course.zgirskaya.state.impl.UnloadingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Port {
  private static final Logger logger = LogManager.getLogger();

  private static class PortHolder {
    private static final Port instance = new Port();
  }

  public static Port getInstance() {
    return PortHolder.instance;
  }

  private Port() {}

  private int capacity;
  private AtomicInteger currentContainers;
  private Semaphore dockSemaphore;

  public String processShip(Ship ship) throws InterruptedException {
    dockSemaphore.acquire();

    try {
      logger.info("Ship {} assigned to dock", ship.getId());

      ship.setState(new UnloadingState());
      while (ship.getContainersToUnload() > 0 && !Thread.currentThread().isInterrupted()) {
        ship.getState().doTask(ship);
      }

      ship.setState(new LoadingState());
      while (ship.getContainersToLoad() > 0 && !Thread.currentThread().isInterrupted()) {
        ship.getState().doTask(ship);
      }

      ship.setState(new CompletedState());
      logger.info("Ship {} processing completed at dock", ship.getId());

      return String.format("Ship %d processed successfully", ship.getId());
    } finally {
      dockSemaphore.release();
    }
  }

  public boolean addContainer() {
    int current = currentContainers.get();
    return current < capacity &&
            currentContainers.compareAndSet(current, current + 1);
  }

  public boolean removeContainer() {
    int current = currentContainers.get();
    return current > 0 &&
            currentContainers.compareAndSet(current, current - 1);
  }

  public int getCurrentContainers() { return currentContainers.get(); }
  public int getCapacity() { return capacity; }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }
  public void setCurrentContainersCount(int currentContainersCount) {
    this.currentContainers = new AtomicInteger(currentContainersCount);
  }
  public void setDocks(int numberOfDocks) {
    this.dockSemaphore = new Semaphore(numberOfDocks, true);
  }
}