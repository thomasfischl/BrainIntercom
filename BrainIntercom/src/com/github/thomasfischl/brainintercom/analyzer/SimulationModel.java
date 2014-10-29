package com.github.thomasfischl.brainintercom.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationModel {

  private List<SimulationModelWindow> windows = new ArrayList<>();

  private List<SimulationModelWindow> negativeExamples = new ArrayList<>();

  private Map<Integer, List<SimulationModelWindow>> positiveExamples = new HashMap<>();

  private List<SimulationModelWindow> currPositiveExamples = null;

  private int windowSize;

  private int dimension;

  public SimulationModel(int windowSize, int dimension, File recFile, double maxValue) throws IOException {
    this.windowSize = windowSize;
    this.dimension = dimension;
    List<int[]> data = loadDataFromFile(recFile, dimension);

    int idx = 0;
    while (idx < data.size() - windowSize) {
      SimulationModelWindow window = new SimulationModelWindow(windowSize, dimension);
      window.setMatch(data.get(idx)[dimension] == 1 ? true : false);

      for (int i = 0; i < windowSize; i++) {
        for (int j = 0; j < dimension; j++) {
          window.data[(i * dimension) + j] = data.get(idx + i)[j];
        }
      }
      windows.add(window);
      idx++;
    }

    for (SimulationModelWindow window : windows) {
      if (!window.shouldMatch()) {
        if (currPositiveExamples != null) {
          positiveExamples.put(positiveExamples.size(), currPositiveExamples);
          currPositiveExamples = null;
        }
        negativeExamples.add(window);
      } else {
        if (currPositiveExamples == null) {
          currPositiveExamples = new ArrayList<>();
        }
        currPositiveExamples.add(window);
      }
    }
  }

  public Map<Integer, List<SimulationModelWindow>> getPositiveExamples() {
    return positiveExamples;
  }

  public List<SimulationModelWindow> getNegativeExamples() {
    return negativeExamples;
  }

  public int getDimension() {
    return dimension;
  }

  public int getWindowSize() {
    return windowSize;
  }

  public List<SimulationModelWindow> getWindows() {
    return windows;
  }

  private List<int[]> loadDataFromFile(File recFile, int dimension) throws IOException, FileNotFoundException {
    // TODO calc correct range
    int range1 = 2;
    int range2 = 8;
    int range3 = 15;

    List<int[]> dataList = new ArrayList<int[]>();
    try (BufferedReader br = new BufferedReader(new FileReader(recFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) {
          continue;
        }

        String[] parts = line.split(";");
        if (parts.length < (3 + dimension)) {
          throw new RuntimeException("No enough dimmentions. Dimensions: " + (parts.length - 3));
        }

        int[] data = new int[dimension + 1];
        data[dimension] = parts[2].isEmpty() ? 0 : 1;

        for (int i = 0; i < dimension; i++) {
          double val = Double.parseDouble(parts[i + 3]);
          if (val < range1) {
            data[i] = 1;
          } else if (val < range2) {
            data[i] = 2;
          } else if (val < range3) {
            data[i] = 3;
          } else {
            data[i] = 4;
          }
        }

        dataList.add(data);
      }
    }
    return dataList;
  }

  public static class SimulationModelWindow {
    public int[] data;
    private int windowSize;
    private int dimension;
    private boolean match;

    public SimulationModelWindow(int windowSize, int dimension) {
      this.windowSize = windowSize;
      this.dimension = dimension;

      data = new int[windowSize * dimension];
    }

    public int getWindowSize() {
      return windowSize;
    }

    public int getDimension() {
      return dimension;
    }

    public void setMatch(boolean match) {
      this.match = match;
    }

    public boolean shouldMatch() {
      return match;
    }

  }

}
