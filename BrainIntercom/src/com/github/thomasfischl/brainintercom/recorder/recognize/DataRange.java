package com.github.thomasfischl.brainintercom.recorder.recognize;

public class DataRange {

  private int range1;
  private int range2;
  private int range3;

  public DataRange(int range1, int range2, int range3) {
    this.range1 = range1;
    this.range2 = range2;
    this.range3 = range3;
  }

  public DataRange() {
  }

  public int getRange(double value) {
    if (value < range1) {
      return 1;
    } else if (value < range2) {
      return 2;
    } else if (value < range3) {
      return 3;
    } else {
      return 4;
    }
  }

  public int getRange(int value) {
    if (value < range1) {
      return 1;
    } else if (value < range2) {
      return 2;
    } else if (value < range3) {
      return 3;
    } else {
      return 4;
    }
  }

  public int getRange1() {
    return range1;
  }

  public int getRange2() {
    return range2;
  }

  public int getRange3() {
    return range3;
  }

}
