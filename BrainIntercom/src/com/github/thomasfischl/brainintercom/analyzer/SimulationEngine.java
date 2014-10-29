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

  public void simulate(Solution individual) {

    long maxErrors = Math.round(size * heatFactor); // about 10%

    int positiv = 0;
    int negativ = simulateNegativeExamples(individual, maxErrors);

    int totalMinMaskDiff = 0;
    for (Entry<Integer, List<SimulationModelWindow>> part : model.getPositiveExamples().entrySet()) {
      boolean match = false;

      int minMaskDiff = Integer.MAX_VALUE;
      for (SimulationModelWindow window : part.getValue()) {
        int countDiff = 0;
        int[] iMask = individual.getMask();
        int[] wMask = window.data;
        for (int idx = 0; idx < size; idx++) {
          if (wMask[idx] != iMask[idx] && iMask[idx] != 0) {
            countDiff++;
          }
        }

        if (countDiff <= maxErrors) {
          // match
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
    }

    int positiveExampleSize = model.getPositiveExamples().size();
    int missingPositivMatch = positiveExampleSize - positiv;

    int fitness = (missingPositivMatch * 100) + (negativ * 100) + (totalMinMaskDiff * 2);
    if (fitness != 0) {
      // a good solution should have a less as possible free places
      fitness += individual.getNumberOfFreePlaces();
    }
    individual.setFitness(fitness);
  }

  private int simulateNegativeExamples(Solution individual, long maxErrors) {
    int negativ = 0;
    for (SimulationModelWindow window : model.getNegativeExamples()) {
      int countDiff = 0;
      int[] iMask = individual.getMask();
      int[] wMask = window.data;
      for (int idx = 0; idx < size; idx++) {
        if (wMask[idx] != iMask[idx] && iMask[idx] != 0) {
          countDiff++;

          if (countDiff > maxErrors) {
            break;
          }
        }
      }

      if (countDiff <= maxErrors) {
        // match
        negativ++;
      }
    }
    return negativ;
  }

}
