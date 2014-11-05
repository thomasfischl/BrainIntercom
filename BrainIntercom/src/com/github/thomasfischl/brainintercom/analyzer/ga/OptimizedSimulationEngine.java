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

  private int numberOfEvents;

  private int maxErrors;

  public OptimizedSimulationEngine(SimulationModel model) {
    this.model = model;
    numberOfEvents = model.getPositiveExamples().size();
    init();
  }

  private void init() {
    List<int[]> vals = model.getData();

    size = vals.size();
    dimension = model.getDimension();
    windowSize = model.getWindowSize();
    data = new int[size * dimension];
    windowType = new int[size];

    for (int row = 0; row < size; row++) {
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

    // maxErrors = Math.round(Math.round(size * heatFactor));
    maxErrors = 0;
  }

  @Override
  public void simulate(Solution[] population, int iteration) {
    Arrays.stream(population).parallel().forEach(obj -> obj.setFitness(simulate(obj.getMask().getData(), numberOfEvents, obj.getNumberOfFreePlaces())));
  }

  private int simulate(int[] mask, int numberOfEvents, int numberOfFreePlaces) {
    int[] result = new int[(size - windowSize) * 2];

    for (int window = 0; window < size - windowSize; window++) {
      int diffCount = 0;
      for (int i = 0; i < mask.length; i++) {
        if (mask[i] == 0) {
          // allow any value
          continue;
        }

        if (mask[i] != data[i + (window * dimension)]) {
          diffCount++;

          if (windowType[window] == 0) {
            if (diffCount > maxErrors) {
              break;
            }
          }

        }
      }
      result[window * 2] = windowType[window];
      result[window * 2 + 1] = diffCount;
    }

    int sumMinDiff = 0;
    int currMinDiff = Integer.MAX_VALUE;
    int positivMatch = 0;
    int negativMatch = 0;
    boolean eventActive = false;
    boolean foundEvent = false;

    for (int i = 0; i < result.length; i += 2) {
      if (result[i] == 0) {
        // should not match

        if (eventActive) {
          eventActive = false;

          if (foundEvent) {
            positivMatch++;
          } else {
            negativMatch++;
          }
          foundEvent = false;

          if (currMinDiff != Integer.MAX_VALUE) {
            sumMinDiff += currMinDiff;
            currMinDiff = Integer.MAX_VALUE;
          }
        }

        if (result[i + 1] == maxErrors) {
          negativMatch++;
        }
      } else {
        // should match
        eventActive = true;
        if (result[i + 1] <= maxErrors) {
          foundEvent = true;
        }
        currMinDiff = Math.min(currMinDiff, result[i + 1]);
      }
    }
    // System.out.println(positivMatch + "/" + negativMatch + "/" + sumMinDiff + "/" + events + "/" + otherData);
    int missingPositivMatch = numberOfEvents - positivMatch;
    return (missingPositivMatch * 500) + (negativMatch * 100) + (sumMinDiff * 2) + numberOfFreePlaces;
  }

}
