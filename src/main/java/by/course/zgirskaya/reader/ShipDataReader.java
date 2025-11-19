package by.course.zgirskaya.reader;

public interface ShipDataReader {
  String DELIMITER = "=";
  void loadConfigurationFromFile(String filename);
}
