package by.course.zgirskaya.state.impl;

import by.course.zgirskaya.entity.Ship;
import by.course.zgirskaya.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WaitingState implements ShipState {
  private static final Logger logger = LogManager.getLogger();
  private static final int WAITING_TIMEOUT = 10;

  @Override
  public void processUntilComplete(Ship ship) {
    logger.info("Ship {} is waiting for free dock", ship.getId());
    delayTask(WAITING_TIMEOUT);
  }
}
