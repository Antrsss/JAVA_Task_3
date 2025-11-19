package by.course.zgirskaya.entity;

import by.course.zgirskaya.state.*;
import by.course.zgirskaya.state.impl.WaitingState;
import by.course.zgirskaya.util.CustomIdGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class Ship implements Callable<String> {
  private static final Logger logger = LogManager.getLogger();

  private final int id;
  private final int capacity;
  private int currentContainers;
  private int containersToLoad;
  private int containersToUnload;
  private ShipState state;

  public Ship(int capacity, int toLoad, int toUnload) {
    this.id = CustomIdGenerator.nextId();
    this.capacity = capacity;
    this.containersToLoad = toLoad;
    this.containersToUnload = toUnload;
    this.currentContainers = toUnload;

    setState(new WaitingState());

    logger.debug("Ship {} created: capacity={}, toUnload={}, toLoad={}",
            id, capacity, toUnload, toLoad);
  }

  @Override
  public String call() {
    logger.info("Ship {} arrived at port (load: {}, unload: {})",
            id, containersToLoad, containersToUnload);

    try {
      Port port = Port.getInstance();
      return port.processShip(this);
    } catch (InterruptedException e) {
      logger.warn("Ship {} processing interrupted", id);
      Thread.currentThread().interrupt();
      return "Interrupted";
    }
  }

  public boolean unloadContainer() {
    Port port = Port.getInstance();
    if (containersToUnload > 0 && port.addContainer()) {
      currentContainers--;
      containersToUnload--;
      return true;
    }
    return false;
  }

  public boolean loadContainer() {
    Port port = Port.getInstance();
    if (containersToLoad > 0 && currentContainers < capacity && port.removeContainer()) {
      currentContainers++;
      containersToLoad--;
      return true;
    }
    return false;
  }

  public void executeUntilComplete() {
    this.state.processUntilComplete(this);
  }

  public int getId() { return id; }
  public int getContainersToLoad() { return containersToLoad; }
  public int getContainersToUnload() { return containersToUnload; }

  public void setState(ShipState state) { this.state = state; }
}