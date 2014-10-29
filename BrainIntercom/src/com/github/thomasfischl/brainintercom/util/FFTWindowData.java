package com.github.thomasfischl.brainintercom.util;

public class FFTWindowData {

  public double[] data;

  private boolean hasEvent;

  public FFTWindowData(double[] data, boolean hasEvent) {
    this.data = data;
    this.hasEvent = hasEvent;
  }

  public FFTWindowData(int size, boolean hasEvent) {
    this.data = new double[size];
    this.hasEvent = hasEvent;
  }

  public FFTWindowData() {
    this.data = new double[0];
  }

  public int getLength() {
    return data.length;
  }

  public FFTWindowData setHasEvent() {
    this.hasEvent = true;
    return this;
  }

  public boolean hasEvent() {
    return hasEvent;
  }

}
