package by.course.zgirskaya.state.impl;

import by.course.zgirskaya.entity.Ship;
import by.course.zgirskaya.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingState implements ShipState {
  private static final Logger logger = LogManager.getLogger();
  private static final int LOADING_TIMEOUT = 30;

  @Override
  public void processUntilComplete(Ship ship) {
    logger.info("Ship {} started loading", ship.getId());

    while (ship.getContainersToLoad() > 0 && !Thread.currentThread().isInterrupted()) {
      if (ship.loadContainer()) {
        logger.debug("Ship {} loaded container (remaining: {})",
                ship.getId(), ship.getContainersToLoad());
      }
      delayTask(LOADING_TIMEOUT);
    }

    logger.info("Ship {} finished loading", ship.getId());
  }
}
