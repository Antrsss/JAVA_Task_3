package by.course.zgirskaya.entity;

import by.course.zgirskaya.state.*;
import by.course.zgirskaya.state.impl.WaitingState;
import by.course.zgirskaya.util.CustomIdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class Ship implements Callable<String> {
  private static final Logger logger = LogManager.getLogger();

  private final int id;
  private final int capacity;
  private final AtomicInteger currentContainers;
  private final AtomicInteger containersToLoad;
  private final AtomicInteger containersToUnload;
  private ShipState state;

  public Ship(int capacity, int toLoad, int toUnload) {
    this.id = CustomIdGenerator.nextId();
    this.capacity = capacity;
    this.containersToLoad = new AtomicInteger(toLoad);
    this.containersToUnload = new AtomicInteger(toUnload);
    this.currentContainers = new AtomicInteger(toUnload);

    setState(new WaitingState());

    logger.debug("Ship {} created: capacity={}, toUnload={}, toLoad={}",
            id, capacity, toUnload, toLoad);
  }

  @Override
  public String call() {
    logger.info("Ship {} arrived at port (load: {}, unload: {})",
            id, containersToLoad.get(), containersToUnload.get());

    try {
      Port port = Port.getInstance();
      return port.processShip(this);
    } catch (InterruptedException e) {
      logger.warn("Ship {} processing interrupted", id);
      Thread.currentThread().interrupt();
      return "Interrupted";
    }
  }

  public void setState(ShipState state) {
    this.state = state;
    this.state.doTask(this);
  }

  public ShipState getState() { return state;}

  public int getId() { return id; }
  public int getCapacity() { return capacity; }
  public int getCurrentContainers() { return currentContainers.get(); }
  public int getContainersToLoad() { return containersToLoad.get(); }
  public int getContainersToUnload() { return containersToUnload.get(); }

  public boolean unloadContainer() {
    Port port = Port.getInstance();
    if (containersToUnload.get() > 0 && port.addContainer()) {
      currentContainers.decrementAndGet();
      containersToUnload.decrementAndGet();
      return true;
    }
    return false;
  }

  public boolean loadContainer() {
    Port port = Port.getInstance();
    if (containersToLoad.get() > 0 && currentContainers.get() < capacity && port.removeContainer()) {
      currentContainers.incrementAndGet();
      containersToLoad.decrementAndGet();
      return true;
    }
    return false;
  }

  public boolean isProcessingComplete() {
    return containersToLoad.get() == 0 && containersToUnload.get() == 0;
  }
}