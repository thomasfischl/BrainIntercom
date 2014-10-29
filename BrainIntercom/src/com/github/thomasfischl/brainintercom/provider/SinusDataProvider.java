package com.github.thomasfischl.brainintercom.provider;

public class SinusDataProvider implements DataProvider {

  private long idx = 0;

  @Override
  public double read() {
    idx++;
    if (idx == Long.MAX_VALUE) {
      idx = 0;
    }

    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    return Math.sin(Math.toRadians((double) idx)) * 200;
  }

  @Override
  public void stop() {
  }

}
