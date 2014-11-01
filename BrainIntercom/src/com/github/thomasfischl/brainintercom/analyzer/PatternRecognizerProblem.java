package com.github.thomasfischl.brainintercom.analyzer;

import java.util.Arrays;

public class PatternRecognizerProblem implements Problem {

  private SimulationEngine engine;
  private SimulationModel model;

  public PatternRecognizerProblem(SimulationModel model) {
    this.model = model;
    engine = new SimulationEngine(model);
  }

  @Override
  public Solution[] createInitialPopulation(int populationSize) {
    Solution[] population = new Solution[populationSize];
    for (int i = 0; i < populationSize; i++) {
      population[i] = new Solution(model.getDimension(), model.getWindowSize(), model.getRange());
      population[i].randomize();
    }
    return population;
  }

  @Override
  public void evaluateFitness(Solution[] population, int iteration) {
    adoptSimulationHeatFactor(iteration);
    Arrays.stream(population).parallel().forEach(obj -> engine.simulate(obj, iteration));
  }

  @Override
  public Solution cross(Solution solution1, Solution solution2, int iteration) {
    return solution1.cross(solution2);
  }

  @Override
  public void mutate(Solution solution, int iteration) {
    solution.mutate(iteration);
  }

  private void adoptSimulationHeatFactor(int iteration) {
    if (iteration == 0) {
      engine.setHeatFactor(0.2);
    } else if (iteration == 50) {
      engine.setHeatFactor(0.1);
    } else if (iteration == 100) {
      engine.setHeatFactor(0.05);
    } else if (iteration == 150) {
      engine.setHeatFactor(0.0);
    } else {
      return;
    }
  }
}
