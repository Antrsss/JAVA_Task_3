package by.course.zgirskaya.state.impl;

import by.course.zgirskaya.entity.Ship;
import by.course.zgirskaya.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnloadingState implements ShipState {
  private static final Logger logger = LogManager.getLogger();
  private static final int UNLOADING_TIMEOUT = 20;

  @Override
  public void processUntilComplete(Ship ship) {
    logger.info("Ship {} started unloading", ship.getId());

    while (ship.getContainersToUnload() > 0 && !Thread.currentThread().isInterrupted()) {
      if (ship.unloadContainer()) {
        logger.debug("Ship {} unloaded container (remaining: {})",
                ship.getId(), ship.getContainersToUnload());
      }
      delayTask(UNLOADING_TIMEOUT);
    }

    logger.info("Ship {} finished unloading", ship.getId());
  }
}
