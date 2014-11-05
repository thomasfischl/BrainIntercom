package com.github.thomasfischl.brainintercom.recorder.recognize;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Recognizer {

  private List<RecognizerPattern> patterns = new ArrayList<>();

  private LinkedList<double[]> windows = new LinkedList<>();

  private int dimenstion;

  private int windowSize;

  private Object monitor = new Object();

  public RecognizerPattern analyzeData(double[] data) {
    synchronized (monitor) {
      if (patterns.isEmpty()) {
        return null;
      }
    }
    windows.add(data);

    if (windows.size() > windowSize * 2) {
      windows.removeFirst();
    }

    if (windows.size() < windowSize) {
      return null;
    }

    int size = dimenstion * windowSize;
    double[] testData = new double[size];

    int startIndex = windows.size() - windowSize;
    for (int row = 0; row < windowSize; row++) {
      double[] val = windows.get(startIndex + row);
      for (int y = 0; y < dimenstion; y++) {
        testData[(row * dimenstion) + y] = val[y];
      }
    }

    synchronized (monitor) {
      for (RecognizerPattern pattern : patterns) {
        if (pattern.match(testData)) {
          return pattern;
        }
      }
    }

    return null;
  }

  public void addPattern(RecognizerPattern pattern) {
    synchronized (monitor) {
      if (patterns.isEmpty()) {
        dimenstion = pattern.getDimension();
        windowSize = pattern.getWindowSize();
      }
      pattern.init();
      patterns.add(pattern);
    }
  }

  public void clearPatterns() {
    synchronized (monitor) {
      patterns.clear();
    }
  }

}
