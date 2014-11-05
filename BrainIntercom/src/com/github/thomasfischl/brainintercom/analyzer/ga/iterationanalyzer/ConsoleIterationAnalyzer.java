package com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer;

import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.google.common.base.Stopwatch;

public class ConsoleIterationAnalyzer implements IIterationAnalyzer {

  @Override
  public void analyze(GA ga, int iteration, Stopwatch duration) {
    System.out.format("%3d iteration: %6d/%6d/%6d %s (%s)\n", iteration, ga.getWorstSolutionFitness(), ga.getAvgSolutionFitness(), ga.getBestSolutionFitness(),
        ga.getBestSolution(), duration);
  }

}
