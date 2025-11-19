package by.course.zgirskaya.main;

import by.course.zgirskaya.entity.Port;
import by.course.zgirskaya.entity.Ship;
import by.course.zgirskaya.reader.impl.ConfigReaderImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
  private static final Logger logger = LogManager.getLogger();
  private static final String CONFIG_FILE = "resources\\config.txt";

  public static void main(String[] args) {
    try {
      ConfigReaderImpl configReader = new ConfigReaderImpl();
      configReader.loadConfigurationFromFile(CONFIG_FILE);

      // Port initialization
      Port port = Port.getInstance();
      port.setCapacity(configReader.getPortCapacity());
      port.setCurrentContainersCount(configReader.getPortCapacity() / 2); // Port is filled on 50%
      port.setDocks(configReader.getNumberOfDocks());

      logger.info("=== PORT SIMULATION STARTED ===");
      logger.info("Port capacity: {}", port.getCapacity());
      logger.info("Current containers in port: {}", port.getCurrentContainers());
      logger.info("Number of docks: {}", configReader.getNumberOfDocks());
      logger.info("Number of ships: {}", configReader.getNumberOfShips());

      // Ships creation
      List<Ship> ships = createShips(configReader, configReader.getNumberOfShips());

      // ThreadPool for ships processing
      ExecutorService executorService = Executors.newFixedThreadPool(configReader.getNumberOfDocks());
      List<Future<String>> results = new ArrayList<>();

      // Ships processing
      for (Ship ship : ships) {
        Future<String> result = executorService.submit(ship);
        results.add(result);
      }

      // Waiting for all tasks completed
      for (Future<String> result : results) {
        try {
          String shipResult = result.get();
          logger.info("Ship result: {}", shipResult);
        } catch (Exception e) {
          logger.error("Error processing ship: {}", e.getMessage());
        }
      }

      // Turn off ExecutorService
      executorService.shutdown();
      logger.info("=== PORT SIMULATION COMPLETED ===");
      logger.info("Final containers in port: {}", port.getCurrentContainers());

    } catch (Exception e) {
      logger.error("Error in main simulation: {}", e.getMessage(), e);
    }
  }

  private static List<Ship> createShips(ConfigReaderImpl configReader, int numberOfShips) {
    List<Ship> ships = new ArrayList<>();
    Random random = new Random();

    int minCapacity = configReader.getMinShipCapacity();
    int maxCapacity = configReader.getMaxShipCapacity();
    int minContainers = configReader.getMinContainers();
    int maxContainers = configReader.getMaxContainers();

    for (int i = 0; i < numberOfShips; i++) {
      // Random ship capacity
      int shipCapacity = random.nextInt(maxCapacity - minCapacity + 1) + minCapacity;

      // Random toUnload & toLoad containers
      int containersToUnload = random.nextInt(Math.min(shipCapacity, maxContainers) - minContainers + 1) + minContainers;
      int containersToLoad = random.nextInt(Math.min(shipCapacity, maxContainers) - minContainers + 1) + minContainers;

      // Checking if all amount < shipCapacity
      if (containersToUnload + containersToLoad > shipCapacity) {
        containersToLoad = shipCapacity - containersToUnload;
        if (containersToLoad < 0) {
          containersToLoad = 0;
          containersToUnload = shipCapacity;
        }
      }

      Ship ship = new Ship(shipCapacity, containersToLoad, containersToUnload);
      ships.add(ship);

      logger.info("Created ship {}: capacity={}, toUnload={}, toLoad={}",
              ship.getId(), shipCapacity, containersToUnload, containersToLoad);
    }

    return ships;
  }
}