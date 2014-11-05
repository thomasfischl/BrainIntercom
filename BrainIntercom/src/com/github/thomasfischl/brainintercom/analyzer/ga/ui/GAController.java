package com.github.thomasfischl.brainintercom.analyzer.ga.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.github.thomasfischl.brainintercom.analyzer.ga.Configuration;
import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.github.thomasfischl.brainintercom.analyzer.ga.PatternRecognizerProblem;
import com.github.thomasfischl.brainintercom.analyzer.ga.SimulationModel;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.ConsoleIterationAnalyzer;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.GenomeAnalyzer;
import com.github.thomasfischl.brainintercom.recorder.recognize.DataRange;
import com.github.thomasfischl.brainintercom.recorder.recognize.RecognizerPattern;
import com.github.thomasfischl.brainintercom.recorder.recognize.RecognizerPatternStore;

public class GAController {

  private ScheduledExecutorService pool;
  private GA ga;
  private UpdaterTask updaterTask;

  private Future<?> gaTask;
  private ScheduledFuture<?> analyzerTask;
  private GAView view;
  private GenomeAnalyzer analyzer = new GenomeAnalyzer();
  private String currRecDataFile;

  public GAController(GAView view) {
    this.view = view;
    pool = Executors.newScheduledThreadPool(2);
    updaterTask = new UpdaterTask(view, analyzer);
    pool.scheduleAtFixedRate(() -> updaterTask.execute(), 0, 100, TimeUnit.MILLISECONDS);
  }

  public void stopGa() {
    stopTask(gaTask);
    stopTask(analyzerTask);
  }

  private void stopTask(Future<?> task) {
    if (task != null) {
      task.cancel(true);
      task = null;
    }
  }

  public void startGa(String file) {
    this.currRecDataFile = file;
    File recFile = new File("./data", file);
    if (!recFile.isFile()) {
      throw new RuntimeException("File '" + recFile.getAbsolutePath() + "' does not exists.");
    }
    gaTask = pool.submit(() -> {
      try {
        analyzer.clear();
        executeGa(recFile);
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    });
  }

  public void executeGa(File recFile) throws IOException {
    SimulationModel model = new SimulationModel(20, 30, recFile, 25);
    showModelMetaData(model);
    showRecordedData(model);

    PatternRecognizerProblem problem = new PatternRecognizerProblem(model);
    ga = new GA(problem);
    updaterTask.setGa(ga);
    ga.addIterationAnalyzer(analyzer);
    ga.addIterationAnalyzer(new ConsoleIterationAnalyzer());
    ga.run();
  }

  private void showModelMetaData(SimulationModel model) {
    StringBuffer txt = new StringBuffer();
    txt.append("Loading Model finished\n");
    txt.append("Windows:           " + model.getWindows().size() + "\n");
    txt.append("Negative Examples: " + model.getNegativeExamples().size() + "\n");
    txt.append("Positive Examples: " + model.getPositiveExamples().size() + "\n");

    DataRange range = model.getRange();
    txt.append("Range            : " + range + "\n");
    txt.append("------------------------------------------\n");
    txt.append("Start GA\n");

    view.updateTextArea(txt.toString());

  }

  private void showRecordedData(SimulationModel model) {
    List<int[]> data = model.getData();

    int[] viewData = new int[data.size() * model.getDimension()];
    for (int row = 0; row < data.size(); row++) {
      for (int col = 0; col < model.getDimension(); col++) {
        viewData[(row * model.getDimension()) + col] = data.get(row)[col];
      }
    }
    view.showRecordedData(viewData, model.getDimension(), data.size());
  }

  public void shutdown() {
    if (pool != null) {
      pool.shutdownNow();
    }
  }

  public void saveBestSolution() {
    RecognizerPatternStore store = new RecognizerPatternStore();
    try {
      RecognizerPattern pattern = ga.getBestSolution().getMask();
      String name = currRecDataFile + "_" + System.currentTimeMillis();
      pattern.setName(name);
      store.storePattern(pattern, new File(Configuration.PATTERN_FOLDER, name + ".json"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
