package com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer;

import java.io.File;
import java.io.IOException;

import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.github.thomasfischl.brainintercom.analyzer.ga.Solution;
import com.github.thomasfischl.brainintercom.recorder.recognize.RecognizerPatternStore;
import com.google.common.base.Stopwatch;

public class SolutionSaver implements IIterationAnalyzer {

  private int oldBestSolution = Integer.MAX_VALUE;

  @Override
  public void analyze(GA ga, int iteration, Stopwatch duration) {

    if (ga.getBestSolutionFitness() < oldBestSolution) {
      storePattern(ga.getBestSolution(), iteration);
    }
    oldBestSolution = Math.min(oldBestSolution, ga.getBestSolutionFitness());
  }

  private void storePattern(Solution bestSol, int iteration) {
    RecognizerPatternStore store = new RecognizerPatternStore();
    try {
      store.storePattern(bestSol.getMask(), new File(GA.RESULT_FOLDER, "pattern_" + iteration + ".json"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
