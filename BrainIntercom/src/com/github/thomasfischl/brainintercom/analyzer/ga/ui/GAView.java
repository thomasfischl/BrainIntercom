package com.github.thomasfischl.brainintercom.analyzer.ga.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import com.github.thomasfischl.brainintercom.analyzer.ga.GA;
import com.github.thomasfischl.brainintercom.analyzer.ga.PatternRecognizerProblem;
import com.github.thomasfischl.brainintercom.analyzer.ga.SimulationModel;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.ConsoleIterationAnalyzer;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.GenomeAnalyzer;
import com.github.thomasfischl.brainintercom.recorder.recognize.DataRange;

public class GAView extends AnchorPane {

  @FXML
  private Slider sliderPopulationSize;
  @FXML
  private LineChart<Integer, Integer> chartQuality;
  @FXML
  private ComboBox<String> cbDataFile;
  @FXML
  private Button btnStart;
  @FXML
  private Label lbPopulationSize;
  @FXML
  private NumberAxis qualityChartYAxis;
  @FXML
  private HBox patternGroup;
  @FXML
  private TextArea txtData;

  private PatternCtrl bestSolPattern;
  private PatternCtrl genomePattern;
  private PatternCtrl dataPattern;

  private ScheduledExecutorService pool;
  private GA ga;
  private Series<Integer, Integer> worstSol;
  private Series<Integer, Integer> avgSol;
  private Series<Integer, Integer> bestSol;
  private UpdaterTask updaterTask;

  public GAView() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GAView.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    initUi();

    pool = Executors.newScheduledThreadPool(2);
    updaterTask = new UpdaterTask(this, bestSolPattern);
    pool.scheduleAtFixedRate(() -> updaterTask.execute(), 0, 100, TimeUnit.MILLISECONDS);

  }

  private void initUi() {
    File folder = new File("./data");
    if (folder.isDirectory()) {
      for (File f : folder.listFiles()) {
        if (f.getName().endsWith(".csv")) {
          cbDataFile.getItems().add(f.getName());
        }
      }
    }
    cbDataFile.getSelectionModel().select(0);

    ObservableList<Series<Integer, Integer>> data = chartQuality.getData();
    data.clear();
    worstSol = new Series<Integer, Integer>();
    avgSol = new Series<Integer, Integer>();
    bestSol = new Series<Integer, Integer>();
    data.add(worstSol);
    data.add(avgSol);
    data.add(bestSol);

    qualityChartYAxis.setAutoRanging(false);
    qualityChartYAxis.setUpperBound(60000);
    qualityChartYAxis.setLowerBound(0);
    qualityChartYAxis.setTickMarkVisible(false);
    // qualityChartYAxis.setTickLabelsVisible(false);
    qualityChartYAxis.setTickUnit(1000);

    bestSolPattern = new PatternCtrl(6, true);
    dataPattern = new PatternCtrl(6, false);

    genomePattern = new PatternCtrl(6, true, (val) -> {
      switch (val) {
      case 0:
        return Color.gray(0);
      case 1:
        return Color.gray(0.2);
      case 2:
        return Color.gray(0.4);
      case 3:
        return Color.gray(0.6);
      case 4:
        return Color.gray(0.8);
      case 5:
        return Color.gray(1);
      default:
        System.out.println(val);
        return Color.RED;
      }
    });

    patternGroup.getChildren().addAll(bestSolPattern, genomePattern, dataPattern);
  }

  @FXML
  private void start(ActionEvent e) {
    File recFile = new File("./data", cbDataFile.getSelectionModel().getSelectedItem());
    if (!recFile.isFile()) {
      throw new RuntimeException("File '" + recFile.getAbsolutePath() + "' does not exists.");
    }
    pool.execute(() -> {
      try {
        executeGa(recFile);
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    });

  }

  public void updateQualityChart(int iteration, int best, int avg, int worst) {
    worstSol.getData().add(new Data<>(iteration, worst));
    avgSol.getData().add(new Data<>(iteration, avg));
    bestSol.getData().add(new Data<>(iteration, best));

    int upperBound = ((avg / 10_000) + 2) * 10_000;
    qualityChartYAxis.setUpperBound(upperBound);
  }

  public void executeGa(File recFile) throws IOException {
    SimulationModel model = new SimulationModel(20, 30, recFile, 25);
    showModelMetaData(model);
    showRecordedData(model);

    PatternRecognizerProblem problem = new PatternRecognizerProblem(model);
    ga = new GA(problem);
    updaterTask.setGa(ga);
    GenomeAnalyzer analyzer = new GenomeAnalyzer();
    pool.scheduleAtFixedRate(() -> updateGenomeView(analyzer, model.getDimension(), model.getWindowSize()), 0, 1, TimeUnit.SECONDS);
    ga.addIterationAnalyzer(analyzer);
    ga.addIterationAnalyzer(new ConsoleIterationAnalyzer());
    ga.run();
  }

  private void updateGenomeView(GenomeAnalyzer analyzer, int dimension, int windowSize) {
    if (analyzer.getGenomes() != null) {
      Platform.runLater(() -> genomePattern.update(analyzer.getGenomes(), dimension, windowSize));
    }
  }

  private void showModelMetaData(SimulationModel model) {
    StringBuffer txt = new StringBuffer();
    txt.append("Loading Model finished\n");
    txt.append("Windows:           " + model.getWindows().size() + "\n");
    txt.append("Negative Examples: " + model.getNegativeExamples().size() + "\n");
    txt.append("Positive Examples: " + model.getPositiveExamples().size() + "\n");

    DataRange range = model.getRange();
    txt.append("Range            : " + range.getRange1() + "/" + range.getRange2() + "/" + range.getRange3() + "\n");
    txt.append("------------------------------------------\n");
    txt.append("Start GA\n");

    Platform.runLater(() -> txtData.setText(txt.toString()));
  }

  private void showRecordedData(SimulationModel model) {
    List<int[]> data = model.getData();

    int[] viewData = new int[data.size() * model.getDimension()];
    for (int row = 0; row < data.size(); row++) {
      for (int col = 0; col < model.getDimension(); col++) {
        viewData[(row * model.getDimension()) + col] = data.get(row)[col];
      }
    }
    Platform.runLater(() -> dataPattern.update(viewData, model.getDimension(), data.size()));
  }

  public void stop() {
    if (pool != null) {
      pool.shutdownNow();
    }
  }

}