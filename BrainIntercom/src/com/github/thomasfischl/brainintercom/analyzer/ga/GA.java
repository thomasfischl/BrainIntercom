package com.github.thomasfischl.brainintercom.analyzer.ga;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.ConsoleIterationAnalyzer;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.IIterationAnalyzer;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.SolutionImageGenerator;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.SolutionSaver;
import com.github.thomasfischl.brainintercom.util.FileUtil;
import com.google.common.base.Stopwatch;

public class GA {

  public static final String RESULT_FOLDER = "C:/tmp/analyzer/results";

  protected Solution[] population;

  public static Random rand = new Random();

  protected int iteration = 0;

  private double mutationRate = 0.1;

  private int avgSolutionFitness;

  private int bestSolutionFitness;

  private int worstSolutionFitness;

  private IProblem problem;

  private List<IIterationAnalyzer> iterationAnalyzer = new ArrayList<>();

  // ------------------------------------------------------------------

  public GA(IProblem problem) {
    this.problem = problem;
  }

  // ------------------------------------------------------------------

  public void run() {
    phaseInit();

    try {
      while (true) {
        runLoop();

        if (bestSolutionFitness == 0) {
          break;
        }

        Thread.sleep(10);
      }
    } catch (InterruptedException e) {
    }
  }

  public void addIterationAnalyzer(IIterationAnalyzer analyzer) {
    iterationAnalyzer.add(analyzer);
  }

  public Solution[] getPopulation() {
    return population;
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

  // ------------------------------------------------------------------

  protected void phaseInit() {
    population = problem.createInitialPopulation(Configuration.populationSize);
  }

  private void phaseEvalFitness() {
    problem.evaluateFitness(population, iteration);
    Arrays.sort(population);
  }

  private void phaseNextGen() {
    Solution[] newPopulation = new Solution[Configuration.populationSize];

    // elitism 1
    newPopulation[0] = population[0];
    int p1;
    int p2;

    // create new population
    for (int i = 1; i < Configuration.populationSize; i++) {
      p1 = selectParentWithTournamet(2);
      p2 = selectParentWithTournamet(2);
      newPopulation[i] = problem.cross(population[p1], (population[p2]), iteration);
    }

    // one shot population change
    population = newPopulation;
  }

  private void phaseMutation() {
    for (int idx = 1; idx < Configuration.populationSize; idx++) {
      if (rand.nextDouble() < mutationRate) {
        problem.mutate(population[idx], iteration);
      }
    }
  }

  private void runLoop() {
    Stopwatch sw = Stopwatch.createStarted();
    phaseEvalFitness();

    analyzeIteration(sw.stop());

    phaseNextGen();
    phaseMutation();

    iteration++;
  }

  private int selectParentWithTournamet(int size) {
    int pos = rand.nextInt(Configuration.populationSize);
    int fitness = population[pos].getFitness();
    double victoryProbablility = rand.nextDouble();

    for (int x = 0; x < size; x++) {
      int tempPos = rand.nextInt(Configuration.populationSize);
      int tempFitness = population[tempPos].getFitness();

      if (tempFitness < fitness && victoryProbablility > .4) {
        fitness = tempFitness;
        pos = tempPos;
      }
    }
    return pos;
  }

  private void analyzeIteration(Stopwatch stopwatch) {
    avgSolutionFitness = 0;
    for (Solution sol : population) {
      avgSolutionFitness += sol.getFitness();
    }

    avgSolutionFitness = avgSolutionFitness / Configuration.populationSize;
    Solution bestSol = population[0];
    bestSolutionFitness = bestSol.getFitness();
    worstSolutionFitness = population[Configuration.populationSize - 1].getFitness();

    for (IIterationAnalyzer analyzer : iterationAnalyzer) {
      analyzer.analyze(this, iteration, stopwatch);
    }
  }

  // -----------------------------------------------------------------------------

  public static void main(String[] args) throws IOException {
    System.out.println("Start loading model");
    // SimulationModel model = new SimulationModel(20, 30, new File("./data/ArduinoDataProvider-1.csv"), 25);
    SimulationModel model = new SimulationModel(20, 30, new File("./data/MicrophoneDataProvider-long-1.csv"), 25);
    // SimulationModel model = new SimulationModel(20, 30, new
    // File("./data/MicrophoneDataProvider-1414575657569.csv"), 25);

    System.out.println("Loading Model finished");

    System.out.println("Windows:           " + model.getWindows().size());
    System.out.println("Negative Examples: " + model.getNegativeExamples().size());
    System.out.println("Positive Examples: " + model.getPositiveExamples().size());

    System.out.println("-------------------------------------------");
    System.out.println("Start GA");

    File folder = new File(RESULT_FOLDER);
    FileUtil.deleteFolder(folder);
    folder.mkdirs();

    PatternRecognizerProblem problem = new PatternRecognizerProblem(model);
    GA ga = new GA(problem);
    ga.addIterationAnalyzer(new ConsoleIterationAnalyzer());
    ga.addIterationAnalyzer(new SolutionImageGenerator());
    ga.addIterationAnalyzer(new SolutionSaver());
    ga.run();

    System.out.println("Finished GA");
    System.out.println("--------------------------------------------");
    System.out.println("Free Places: " + ga.getBestSolution().getNumberOfFreePlaces() + " of " + (model.getWindowSize() * model.getDimension()));
    System.out.println("Iteration: " + ga.iteration);
  }

}
