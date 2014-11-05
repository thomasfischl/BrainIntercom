package com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer;

import java.io.File;
import java.io.IOException;

import com.github.thomasfischl.brainintercom.analyzer.ga.Configuration;
import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.github.thomasfischl.brainintercom.analyzer.ga.ImageGenerator;
import com.github.thomasfischl.brainintercom.analyzer.ga.Solution;
import com.google.common.base.Stopwatch;

public class SolutionImageGenerator implements IIterationAnalyzer {

  private int oldBestSolution = Integer.MAX_VALUE;

  @Override
  public void analyze(GA ga, int iteration, Stopwatch duration) {

    if (ga.getBestSolutionFitness() < oldBestSolution) {
      generatePatternImage(ga.getBestSolution(), iteration);
    }
    oldBestSolution = Math.min(oldBestSolution, ga.getBestSolutionFitness());
  }

  private void generatePatternImage(Solution bestSol, int iteration) {
    try {
      int windowSize = bestSol.getMask().getWindowSize();
      int dimenstion = bestSol.getMask().getDimension();
      int[] data = bestSol.getMask().getData();
      ImageGenerator.generateWindowImage(new File(Configuration.RESULT_FOLDER, "iteration" + iteration + ".png"), windowSize, dimenstion, data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
