package com.github.thomasfischl.brainintercom.analyzer.ga.ui;

import javafx.application.Platform;

import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.github.thomasfischl.brainintercom.recorder.recognize.RecognizerPattern;

public class UpdaterTask {

  private GA ga;
  private GAView view;
  private int lastIteration = -1;
  private PatternCtrl bestSolCanvas;

  public UpdaterTask(GAView view, PatternCtrl bestSolPattern) {
    this.view = view;
    this.bestSolCanvas = bestSolPattern;
  }

  public void execute() {
    if (ga == null) {
      return;
    }
    if (lastIteration == ga.getIteration()) {
      return;
    }

    Platform.runLater(() -> {
      view.updateQualityChart(ga.getIteration(), ga.getBestSolutionFitness(), ga.getAvgSolutionFitness(), ga.getWorstSolutionFitness());
      RecognizerPattern mask = ga.getBestSolution().getMask();
      bestSolCanvas.update(mask.getData(), mask.getDimenstion(), mask.getWindowSize());
    });

    lastIteration = ga.getIteration();
  }

  public void setGa(GA ga) {
    this.ga = ga;
  }

}
