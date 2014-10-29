package com.github.thomasfischl.brainintercom.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import com.github.thomasfischl.brainintercom.util.FileUtil;
import com.google.common.base.Stopwatch;

public class GA {

  private static final String RESULT_FOLDER = "C:/tmp/analyzer/results";

  private int populationSize = 300;

  private Solution[] population;

  private SimulationModel model;

  public static Random rand = new Random();

  private int iteration = 0;

  private double mutationRate = 0.2;

  private SimulationEngine engine;

  public GA(SimulationModel model) {
    this.model = model;
    engine = new SimulationEngine(model);
  }

  private void phaseInit() {
    population = new Solution[populationSize];
    int windowSize = model.getWindowSize();
    int dimension = model.getDimension();
    for (int i = 0; i < populationSize; i++) {
      population[i] = new Solution(windowSize, dimension);
      population[i].randomize();
    }
  }

  private void phaseEvalFitness() {
    Arrays.stream(population).parallel().forEach(obj -> engine.simulate(obj));
    // for (int i = 0; i < populationSize; i++) {
    // engine.simulate(population[i]);
    // }

    Arrays.sort(population);
  }

  private void phaseNextGen() {
    Solution[] newPopulation = new Solution[populationSize];

    // elitism 1
    newPopulation[0] = population[0];
    int p1;
    int p2;

    // create new population
    for (int i = 1; i < populationSize; i++) {
      p1 = selectParentWithTournamet(3);
      p2 = selectParentWithTournamet(3);
      newPopulation[i] = population[p1].cross(population[p2]);
    }

    // one shot population change
    population = newPopulation;
  }

  private int selectParentWithTournamet(int size) {
    int pos = rand.nextInt(populationSize);
    int fitness = population[pos].getFitness();

    for (int x = 0; x < 5; x++) {
      int tempPos = rand.nextInt(populationSize);
      int tempFitness = population[tempPos].getFitness();

      if (tempFitness < fitness) {
        fitness = tempFitness;
        pos = tempPos;
      }
    }
    return pos;
  }

  private void phaseMutation() {
    for (int idx = 1; idx < populationSize; idx++) {
      if (rand.nextDouble() < mutationRate) {
        population[idx].mutate(iteration);
      }
    }
  }

  public void run() {
    phaseInit();
    Stopwatch sw = Stopwatch.createStarted();

    while (true) {
      sw.reset();
      sw.start();
      phaseEvalFitness();
      sw.stop();
      analyzeIteration(sw);

      phaseNextGen();
      phaseMutation();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }

      if (iteration == 10) {
        engine.setHeatFactor(0.4);
        oldBestSolution = Integer.MAX_VALUE;
      } else if (iteration == 20) {
        engine.setHeatFactor(0.3);
        oldBestSolution = Integer.MAX_VALUE;
      } else if (iteration == 40) {
        engine.setHeatFactor(0.2);
        oldBestSolution = Integer.MAX_VALUE;
      } else if (iteration == 60) {
        engine.setHeatFactor(0.1);
        oldBestSolution = Integer.MAX_VALUE;
      } else if (iteration == 100) {
        engine.setHeatFactor(0.05);
        oldBestSolution = Integer.MAX_VALUE;
      } else if (iteration == 150) {
        engine.setHeatFactor(0.0);
        oldBestSolution = Integer.MAX_VALUE;
      }

      if (oldBestSolution == 0) {
        break;
      }

      iteration++;
    }
  }

  private int oldBestSolution = Integer.MAX_VALUE;

  private void analyzeIteration(Stopwatch sw) {
    int avg = 0;
    for (Solution sol : population) {
      avg += sol.getFitness();
    }

    avg = avg / populationSize;
    int best = population[0].getFitness();
    int worst = population[populationSize - 2].getFitness();

    System.out.format("%3d iteration: %6d/%6d/%6d (%s)\n", iteration, worst, avg, best, sw.toString());

    if (best < oldBestSolution) {
      try {
        ImageGenerator.generateWindowImage(new File(RESULT_FOLDER, "iteration" + iteration + ".png"), model.getWindowSize(),
            model.getDimension(), population[0].getMask());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    oldBestSolution = best;
  }

  public Solution getBestSolution() {
    return population[0];
  }

  // -----------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    System.out.println("Start loading model");
    SimulationModel model = new SimulationModel(20, 30, new File("./data/ArduinoDataProvider-1414530526911.csv"), 25);
    // SimulationModel model = new SimulationModel(20, 30, new File("./data/MicrophoneDataProvider-1414498984805.csv"), 25);
    System.out.println("Loading Model finished");

    System.out.println("Negative Examples: " + model.getNegativeExamples().size());
    System.out.println("Positive Examples: " + model.getPositiveExamples().size());

    System.out.println("-------------------------------------------");
    System.out.println("Start GA");

    File folder = new File(RESULT_FOLDER);
    FileUtil.deleteFolder(folder);
    folder.mkdirs();

    GA ga = new GA(model);
    ga.run();

    System.out.println("Finished GA");
    System.out.println("--------------------------------------------");
    System.out.println("Free Places: " + ga.getBestSolution().getNumberOfFreePlaces() + " of "
        + (model.getWindowSize() * model.getDimension()));
    System.out.println("Iteration: " + ga.iteration);
  }

}
