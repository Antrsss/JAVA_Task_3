package by.course.zgirskaya.state.impl;

import by.course.zgirskaya.entity.Ship;
import by.course.zgirskaya.state.ShipState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompletedState implements ShipState {
  private static final Logger logger = LogManager.getLogger();
  private static final int COMPLETING_TIMEOUT = 5;

  @Override
  public void doTask(Ship ship) {
    logger.info("Ship {} completed processing and leaving port", ship.getId());
    delayTask(COMPLETING_TIMEOUT);
  }
}
