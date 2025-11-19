package by.course.zgirskaya.util;

public class CustomIdGenerator {
  private static int idCounter;
  public static int nextId() { return ++idCounter; }

  private CustomIdGenerator() {}
}
