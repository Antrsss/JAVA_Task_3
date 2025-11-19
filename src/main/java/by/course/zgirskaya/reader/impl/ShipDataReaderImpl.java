package by.course.zgirskaya.reader.impl;

import by.course.zgirskaya.reader.ShipDataReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ShipDataReaderImpl implements ShipDataReader {
  private static final Logger logger = LogManager.getLogger();

  private int portCapacity;
  private int numberOfDocks;
  private int numberOfShips;
  private int minShipCapacity;
  private int maxShipCapacity;
  private int minContainers;
  private int maxContainers;

  public void loadConfigurationFromFile(String filename) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(DELIMITER);
        if (parts.length == 2) {
          String key = parts[0].trim();
          String value = parts[1].trim();

          switch (key) {
            case "port_capacity":
              portCapacity = Integer.parseInt(value);
              break;
            case "number_of_docks":
              numberOfDocks = Integer.parseInt(value);
              break;
            case "number_of_ships":
              numberOfShips = Integer.parseInt(value);
              break;
            case "min_ship_capacity":
              minShipCapacity = Integer.parseInt(value);
              break;
            case "max_ship_capacity":
              maxShipCapacity = Integer.parseInt(value);
              break;
            case "min_containers":
              minContainers = Integer.parseInt(value);
              break;
            case "max_containers":
              maxContainers = Integer.parseInt(value);
              break;
          }
        }
      }
      logger.info("Configuration loaded successfully");
    } catch (IOException e) {
      logger.error("Error loading configuration: {}", e.getMessage());
    }
  }

  public int getPortCapacity() { return portCapacity; }
  public int getNumberOfDocks() { return numberOfDocks; }
  public int getNumberOfShips() { return numberOfShips; }
  public int getMinShipCapacity() { return minShipCapacity; }
  public int getMaxShipCapacity() { return maxShipCapacity; }
  public int getMinContainers() { return minContainers; }
  public int getMaxContainers() { return maxContainers; }
}