package com.github.thomasfischl.brainintercom.analyzer.ga.ui;

import javafx.application.Platform;

import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.GenomeAnalyzer;
import com.github.thomasfischl.brainintercom.recorder.recognize.RecognizerPattern;

public class UpdaterTask {

  private GA ga;
  private GAView view;
  private int lastIteration = -1;
  private GenomeAnalyzer analyzer;

  public UpdaterTask(GAView ctrl, GenomeAnalyzer analyzer) {
    this.view = ctrl;
    this.analyzer = analyzer;
  }

  public void execute() {
    if (ga == null) {
      return;
    }
    if (lastIteration == ga.getIteration()) {
      return;
    }

    Platform.runLater(() -> {
      RecognizerPattern mask = ga.getBestSolution().getMask();
      int dimension = mask.getDimension();
      int windowSize = mask.getWindowSize();

      view.updateState(ga.getIteration(), ga.getBestSolutionFitness(), ga.getAvgSolutionFitness(), ga.getWorstSolutionFitness());
      view.updateBestSolutions(mask.getData(), dimension, windowSize, ga.getBestSolution() );
      view.updateGenomeView(analyzer, dimension, windowSize);
    });

    lastIteration = ga.getIteration();
  }

  public void setGa(GA ga) {
    this.ga = ga;
  }

}
