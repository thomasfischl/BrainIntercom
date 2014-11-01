package com.github.thomasfischl.brainintercom.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import com.github.thomasfischl.brainintercom.recorder.recognize.RecognizerPatternStore;
import com.github.thomasfischl.brainintercom.util.FileUtil;
import com.google.common.base.Stopwatch;

public class GA {

  public static final String RESULT_FOLDER = "C:/tmp/analyzer/results";

  protected int populationSize = 500;

  protected Solution[] population;

  public static SimulationModel model;

  public static Random rand = new Random();

  protected int iteration = 0;

  private double mutationRate = 0.1;

  private SimulationEngine engine;

  private int oldBestSolution = Integer.MAX_VALUE;

  private int avgSolutionFitness;

  private int bestSolutionFitness;

  private int worstSolutionFitness;

  private boolean analyzeIteration;

  public GA(SimulationModel model, boolean analyzeIteration) {
    GA.model = model;
    this.analyzeIteration = analyzeIteration;
    engine = new SimulationEngine(model);
  }

  public void run() {
    phaseInit();

    try {
      while (true) {
        runLoop();

        if (oldBestSolution == 0) {
          break;
        }

        Thread.sleep(10);
      }
    } catch (InterruptedException e) {
    }
  }

  protected void phaseInit() {
    population = new Solution[populationSize];
    for (int i = 0; i < populationSize; i++) {
      population[i] = new Solution();
      population[i].randomize();
    }
  }

  private void phaseEvalFitness() {
    Arrays.stream(population).parallel().forEach(obj -> engine.simulate(obj, iteration));
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
      p1 = selectParentWithTournamet(2);
      p2 = selectParentWithTournamet(2);
      newPopulation[i] = population[p1].cross(population[p2]);
    }

    // one shot population change
    population = newPopulation;
  }

  private void phaseMutation() {
    for (int idx = 1; idx < populationSize; idx++) {
      if (rand.nextDouble() < mutationRate) {
        population[idx].mutate(iteration);
      }
    }
  }

  private void runLoop() {
    adoptSimulationHeatFactor();

    Stopwatch sw = Stopwatch.createStarted();
    phaseEvalFitness();

    if (analyzeIteration) {
      analyzeIteration(sw.stop().toString());
    }

    phaseNextGen();
    phaseMutation();

    iteration++;
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

  private void adoptSimulationHeatFactor() {
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
    oldBestSolution = Integer.MAX_VALUE;
  }

  private void analyzeIteration(String duration) {
    avgSolutionFitness = 0;
    for (Solution sol : population) {
      avgSolutionFitness += sol.getFitness();
    }

    avgSolutionFitness = avgSolutionFitness / populationSize;
    Solution bestSol = population[0];
    bestSolutionFitness = bestSol.getFitness();
    worstSolutionFitness = population[populationSize - 2].getFitness();

    System.out.format("%3d iteration: %6d/%6d/%6d %s (%s)\n", iteration, worstSolutionFitness, avgSolutionFitness, bestSolutionFitness,
        bestSol, duration);

    if (bestSolutionFitness < oldBestSolution) {
      generatePatternImage(bestSol);
    }
    if (iteration % 20 == 0) {
      storePattern(bestSol);
    }
    oldBestSolution = bestSolutionFitness;
  }

  private void storePattern(Solution bestSol) {
    RecognizerPatternStore store = new RecognizerPatternStore();
    try {
      store.storePattern(bestSol.getMask(), new File(RESULT_FOLDER, "pattern_" + iteration + ".json"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void generatePatternImage(Solution bestSol) {
    try {
      int windowSize = bestSol.getMask().getWindowSize();
      int dimenstion = bestSol.getMask().getDimenstion();
      int[] data = bestSol.getMask().getData();
      ImageGenerator.generateWindowImage(new File(RESULT_FOLDER, "iteration" + iteration + ".png"), windowSize, dimenstion, data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Solution getBestSolution() {
    return population[0];
  }

  public int getIteration() {
    return iteration;
  }

  public int getAvgSolutionFitness() {
    return avgSolutionFitness;
  }

  public int getBestSolutionFitness() {
    return bestSolutionFitness;
  }

  public int getWorstSolutionFitness() {
    return worstSolutionFitness;
  }

  // -----------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    System.out.println("Start loading model");
    // SimulationModel model = new SimulationModel(40, 30, new File("./data/ArduinoDataProvider-1.csv"), 25);
    SimulationModel model = new SimulationModel(20, 30, new File("./data/MicrophoneDataProvider-long-1.csv"), 25);
    // SimulationModel model = new SimulationModel(20, 30, new File("./data/MicrophoneDataProvider-1414575657569.csv"), 25);

    System.out.println("Loading Model finished");

    System.out.println("Windows:           " + model.getWindows().size());
    System.out.println("Negative Examples: " + model.getNegativeExamples().size());
    System.out.println("Positive Examples: " + model.getPositiveExamples().size());

    System.out.println("-------------------------------------------");
    System.out.println("Start GA");

    File folder = new File(RESULT_FOLDER);
    FileUtil.deleteFolder(folder);
    folder.mkdirs();

    GA ga = new GA(model, true);
    ga.run();

    System.out.println("Finished GA");
    System.out.println("--------------------------------------------");
    System.out.println("Free Places: " + ga.getBestSolution().getNumberOfFreePlaces() + " of "
        + (model.getWindowSize() * model.getDimension()));
    System.out.println("Iteration: " + ga.iteration);
  }

}
