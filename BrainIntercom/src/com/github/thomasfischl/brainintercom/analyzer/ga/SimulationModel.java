package com.github.thomasfischl.brainintercom.analyzer.ga;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thomasfischl.brainintercom.recorder.recognize.DataRange;

public class SimulationModel {

  private List<SimulationModelWindow> windows = new ArrayList<>();

  private List<SimulationModelWindow> negativeExamples = new ArrayList<>();

  private Map<Integer, List<SimulationModelWindow>> positiveExamples = new HashMap<>();

  private List<SimulationModelWindow> currPositiveExamples = null;

  private int windowSize;

  private int dimension;

  private DataRange range;

  private List<int[]> data;

  public SimulationModel(int windowSize, int dimension, File recFile, double maxValue) throws IOException {
    this.windowSize = windowSize;
    this.dimension = dimension;

    List<double[]> rawData = loadDataFromFile(recFile);
    data = processData(rawData);
    covertDataToWindows(data);
    classifyWindows();
  }

  public List<int[]> getData() {
    return data;
  }

  private List<int[]> processData(List<double[]> rawData) {
    List<Integer> list = new ArrayList<Integer>();
    for (double[] integer : rawData) {
      for (double val : integer) {
        list.add((int) val);
      }
    }

    Collections.sort(list);

    int range1 = list.get((int) (list.size() * 0.3)).intValue();
    int range2 = list.get((int) (list.size() * 0.5)).intValue();
    int range3 = list.get((int) (list.size() * 0.8)).intValue();

    if (range2 <= range1) {
      range2 = range1 + 1;
    }

    if (range3 <= range2) {
      range3 = range2 + 1;
    }

    range = new DataRange(range1, range2, range3);
    System.out.println("Range: " + range.getRange1() + "/" + range.getRange2() + "/" + range.getRange3());

    List<int[]> result = new ArrayList<int[]>();
    for (double[] raw : rawData) {
      int[] data = new int[raw.length];
      for (int i = 0; i < data.length - 1; i++) {
        data[i] = range.getRange(raw[i]);
      }

      data[data.length - 1] = (int) raw[data.length - 1];
      result.add(data);
    }
    return result;
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

  public DataRange getRange() {
    return range;
  }

  // ----------------------------------------------------------------------------------------

  private List<double[]> loadDataFromFile(File recFile) throws IOException, FileNotFoundException {
    List<double[]> dataList = new ArrayList<double[]>();
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

        double[] data = new double[dimension + 1];
        data[dimension] = parts[2].isEmpty() ? 0 : 1;

        for (int i = 0; i < dimension; i++) {
          data[i] = Double.parseDouble(parts[i + 3]);
        }
        dataList.add(data);
      }
    }
    return dataList;
  }

  private void covertDataToWindows(List<int[]> data) {
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
  }

  private void classifyWindows() {
    for (SimulationModelWindow window : windows) {
      if (!window.shouldMatch()) {
        if (currPositiveExamples != null) {
          currPositiveExamples = null;
        }
        negativeExamples.add(window);
      } else {
        if (currPositiveExamples == null) {
          currPositiveExamples = new ArrayList<>();
          positiveExamples.put(positiveExamples.size(), currPositiveExamples);
        }
        currPositiveExamples.add(window);
      }
    }
  }

  // ----------------------------------------------------------------------------------------

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
