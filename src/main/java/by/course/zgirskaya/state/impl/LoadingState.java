package by.course.zgirskaya.state.impl;

import by.course.zgirskaya.entity.Ship;
import by.course.zgirskaya.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingState implements ShipState {
  private static final Logger logger = LogManager.getLogger();
  private static final int LOADING_TIMEOUT = 20;

  @Override
  public void doTask(Ship ship) {
    if (ship.loadContainer()) {
      logger.debug("Ship {} loaded container (remaining: {})",
              ship.getId(), ship.getContainersToLoad());
    }
    delayTask(LOADING_TIMEOUT);
  }
}
