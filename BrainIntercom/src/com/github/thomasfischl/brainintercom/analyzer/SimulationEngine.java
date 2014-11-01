package com.github.thomasfischl.brainintercom.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.github.thomasfischl.brainintercom.analyzer.SimulationModel.SimulationModelWindow;

public class SimulationEngine {

  private SimulationModel model;

  private final int size;

  private List<int[]> optModel = new ArrayList<>();

  private double heatFactor = 0.5;

  public SimulationEngine(SimulationModel model) {
    this.model = model;
    size = model.getDimension() * model.getWindowSize();
    for (SimulationModelWindow window : model.getWindows()) {
      optModel.add(window.data);
    }
  }

  public void setHeatFactor(double heatFactor) {
    System.out.println("Set HeatFactor: " + heatFactor + " (" + this.heatFactor + ")");
    this.heatFactor = heatFactor;
  }

  public void simulate(Solution individual, int iteration) {

    int maxErrors = Math.round(Math.round(size * heatFactor)); // about 10%

    int positiv = 0;
    int negativ = simulateNegativeExamples(individual, maxErrors);

    int totalMinMaskDiff = 0;

    int positiveExampleSize = model.getPositiveExamples().size();

    int counter = 1;

    for (Entry<Integer, List<SimulationModelWindow>> part : model.getPositiveExamples().entrySet()) {
      if (counter > positiveExampleSize) {
        break;
      }
      boolean match = false;

      int minMaskDiff = Integer.MAX_VALUE;
      for (SimulationModelWindow window : part.getValue()) {
        int countDiff = individual.getMask().calcDiffCount(window.data, -1);
        if (countDiff <= maxErrors) {
          match = true;
        }
        minMaskDiff = Math.min(minMaskDiff, countDiff);
      }
      totalMinMaskDiff += minMaskDiff;

      if (match) {
        positiv++;
      } else {
        negativ++;
      }
      counter++;
    }

    int missingPositivMatch = positiveExampleSize - positiv;
    individual.calcFitness(positiv, negativ, missingPositivMatch, totalMinMaskDiff);
  }

  private int simulateNegativeExamples(Solution individual, int maxErrors) {
    int negativ = 0;
    for (SimulationModelWindow window : model.getNegativeExamples()) {
      int countDiff = individual.getMask().calcDiffCount(window.data, maxErrors);
      if (countDiff <= maxErrors) {
        negativ++;
      }
    }
    return negativ;
  }

}
