package com.github.thomasfischl.brainintercom.analyzer.ga;

import java.util.Arrays;
import java.util.List;

public class OptimizedSimulationEngine implements ISimulationEngine {

  private SimulationModel model;

  private double heatFactor = 0.5;

  private int[] data;

  private int[] windowType;

  private int size;

  private int dimension;

  private int windowSize;

  public OptimizedSimulationEngine(SimulationModel model) {
    this.model = model;
    init();
  }

  private void init() {
    List<int[]> vals = model.getData();

    size = vals.size();
    dimension = model.getDimension();
    windowSize = model.getWindowSize();
    data = new int[vals.size() * dimension];
    windowType = new int[vals.size()];

    for (int row = 0; row < vals.size(); row++) {
      for (int col = 0; col < dimension; col++) {
        data[(row * dimension) + col] = vals.get(row)[col];
      }

      windowType[row] = vals.get(row)[dimension];
    }
  }

  @Override
  public void setHeatFactor(double heatFactor) {
    System.out.println("Set HeatFactor: " + heatFactor + " (" + this.heatFactor + ")");
    this.heatFactor = heatFactor;
  }

  @Override
  public void simulate(Solution[] population, int iteration) {
    Arrays.stream(population).parallel().forEach(obj -> simulate(obj, iteration));
  }

  private void simulate(Solution obj, int iteration) {
    int[] mask = obj.getMask().getData();

    int[] result = new int[size - windowSize * 2];

    for (int window = 0; window < size - windowSize; window++) {
      int diffCount = 0;
      for (int i = 0; i < mask.length; i++) {
        if (mask[i] == 0) {
          // allow any value
          continue;
        }

        if (mask[i] != data[i + (window * dimension)]) {
          diffCount++;
        }
      }
      result[window * 2] = windowType[window];
      result[window * 2 + 1] = diffCount;
    }

  }

  // private void simulate1(Solution individual, int iteration) {
  //
  // int maxErrors = Math.round(Math.round(size * heatFactor)); // about 10%
  //
  // int positiv = 0;
  // int negativ = simulateNegativeExamples(individual, maxErrors);
  //
  // int totalMinMaskDiff = 0;
  //
  // int positiveExampleSize = model.getPositiveExamples().size();
  //
  // int counter = 1;
  //
  // for (Entry<Integer, List<SimulationModelWindow>> part : model.getPositiveExamples().entrySet()) {
  // if (counter > positiveExampleSize) {
  // break;
  // }
  // boolean match = false;
  //
  // int minMaskDiff = Integer.MAX_VALUE;
  // for (SimulationModelWindow window : part.getValue()) {
  // int countDiff = individual.getMask().calcDiffCount(window.data, -1);
  // if (countDiff <= maxErrors) {
  // match = true;
  // }
  // minMaskDiff = Math.min(minMaskDiff, countDiff);
  // }
  // totalMinMaskDiff += minMaskDiff;
  //
  // if (match) {
  // positiv++;
  // } else {
  // negativ++;
  // }
  // counter++;
  // }
  //
  // int missingPositivMatch = positiveExampleSize - positiv;
  // individual.calcFitness(positiv, negativ, missingPositivMatch, totalMinMaskDiff);
  // }

  // private int simulateNegativeExamples(Solution individual, int maxErrors) {
  // int negativ = 0;
  // for (SimulationModelWindow window : model.getNegativeExamples()) {
  // int countDiff = individual.getMask().calcDiffCount(window.data, maxErrors);
  // if (countDiff <= maxErrors) {
  // negativ++;
  // }
  // }
  // return negativ;
  // }

}
