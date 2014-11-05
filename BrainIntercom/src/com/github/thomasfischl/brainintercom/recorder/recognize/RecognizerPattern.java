package com.github.thomasfischl.brainintercom.recorder.recognize;

import com.google.common.base.Preconditions;

public class RecognizerPattern {

  protected int[] data;
  private transient int[] tmpData;
  private String name;
  private DataRange range;
  private int dimenstion;
  private int windowSize;
  private transient int size;

  public RecognizerPattern(String name, int dimenstion, int windowSize, DataRange range) {
    this.name = name;
    this.range = range;
    this.dimenstion = dimenstion;
    this.windowSize = windowSize;
    init();
    this.data = new int[size];
  }

  public RecognizerPattern() {
  }

  public void init() {
    size = dimenstion * windowSize;
    tmpData = new int[size];
  }

  public boolean match(double[] vals) {
    Preconditions.checkArgument(vals.length == size, vals.length + "!=" + size);

    for (int i = 0; i < data.length; i++) {
      tmpData[i] = range.getRange(vals[i]);
    }

    return calcDiffCount(tmpData, -1) == 0;
  }

  public int calcDiffCount(int[] vals, int maxError) {
    Preconditions.checkArgument(vals.length == size);

    int diffCount = 0;
    for (int i = 0; i < data.length; i++) {
      if (data[i] == 0) {
        // allow any value
        continue;
      }

      if (data[i] != vals[i]) {
        diffCount++;

        if (maxError != -1 && diffCount > maxError) {
          break;
        }
      }
    }

    return diffCount;
  }

  public void setRange(DataRange range) {
    this.range = range;
  }

  public int[] getData() {
    return data;
  }

  public DataRange getRange() {
    return range;
  }

  public String getName() {
    return name;
  }

  public int getDimension() {
    return dimenstion;
  }

  public int getWindowSize() {
    return windowSize;
  }

  public int getSize() {
    return size;
  }

  public void setName(String name) {
    this.name = name;
  }

}
