package com.github.thomasfischl.brainintercom.recorder.recognize;

public class DataRange {

  private int[] ranges;

  public DataRange(int[] ranges) {
    this.ranges = ranges;
  }

  public DataRange() {
  }

  public int getRange(int value) {
    for (int i = 0; i < ranges.length; i++) {
      if (value < ranges[i]) {
        return i + 1;
      }
    }
    return ranges.length;
  }

  public int getRange(double value) {
    return getRange((int) Math.round(value));
  }

  public int[] getRanges() {
    return ranges;
  }

  @Override
  public String toString() {
    String text = "{";

    for (int i = 0; i < ranges.length; i++) {
      if (i == 0) {
        text += ranges[i];
      } else {
        text += ", " + ranges[i];
      }
    }

    return text + "}";
  }

}
