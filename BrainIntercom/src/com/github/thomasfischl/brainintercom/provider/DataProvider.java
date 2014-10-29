package com.github.thomasfischl.brainintercom.provider;

public interface DataProvider {

  public static double NO_DATA = Double.MAX_VALUE;

  double read();

  void stop();

  default boolean hasEvent() {
    return false;
  }

}
