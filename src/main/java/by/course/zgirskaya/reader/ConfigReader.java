package by.course.zgirskaya.reader;

public interface ConfigReader {
  String DELIMITER = "=";
  void loadConfigurationFromFile(String filename);
}
