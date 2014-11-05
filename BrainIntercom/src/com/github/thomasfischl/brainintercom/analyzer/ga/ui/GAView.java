package com.github.thomasfischl.brainintercom.analyzer.ga.ui;

import java.io.File;
import java.io.IOException;

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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import com.github.thomasfischl.brainintercom.analyzer.ga.Configuration;
import com.github.thomasfischl.brainintercom.analyzer.ga.Solution;
import com.github.thomasfischl.brainintercom.analyzer.ga.iterationanalyzer.GenomeAnalyzer;

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

  private Series<Integer, Integer> worstSol;
  private Series<Integer, Integer> avgSol;
  private Series<Integer, Integer> bestSol;

  private boolean running;

  @FXML
  private ProgressBar pbFoundMatches;
  @FXML
  private ProgressBar pbNumberFreePlaces;
  private GAController controller = new GAController(this);

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
    qualityChartYAxis.setTickUnit(1000);

    bestSolPattern = new PatternCtrl(6, true);
    dataPattern = new PatternCtrl(6, false);
    genomePattern = new PatternCtrl(6, true, new GrayColorMapper(Configuration.dataRangeSize));
    patternGroup.getChildren().addAll(bestSolPattern, genomePattern, dataPattern);
  }

  @FXML
  private void start(ActionEvent e) {
    if (running) {
      controller.stopGa();
      btnStart.setText("Start");
      running = false;

    } else {
      reset();
      btnStart.setText("Stop");
      running = true;
      controller.startGa(cbDataFile.getSelectionModel().getSelectedItem());
    }
  }

  private void reset() {
    worstSol.getData().clear();
    avgSol.getData().clear();
    bestSol.getData().clear();

    pbFoundMatches.setProgress(0);
    pbNumberFreePlaces.setProgress(0);
  }

  public void updateState(int iteration, int best, int avg, int worst) {
    worstSol.getData().add(new Data<>(iteration, worst));
    avgSol.getData().add(new Data<>(iteration, avg));
    bestSol.getData().add(new Data<>(iteration, best));

    int upperBound = ((avg / 10_000) + 2) * 10_000;
    qualityChartYAxis.setUpperBound(upperBound);
  }

  public void updateGenomeView(GenomeAnalyzer analyzer, int dimension, int windowSize) {
    if (analyzer.getGenomes() != null) {
      Platform.runLater(() -> genomePattern.update(analyzer.getGenomes(), dimension, windowSize));
    }
  }

  public void stop() {
    controller.shutdown();
  }

  public void updateBestSolutions(int[] data, int dimension, int windowSize, Solution bestSolution) {
    bestSolPattern.update(data, dimension, windowSize);

    double positveWindows = bestSolution.getMissingPositivMatch() + bestSolution.getPositivMatch();
    pbFoundMatches.setProgress((double) bestSolution.getPositivMatch() / positveWindows);

    double size = bestSolution.getMask().getSize();
    pbNumberFreePlaces.setProgress((double) bestSolution.getNumberOfFreePlaces() / size);
  }

  public void updateTextArea(String text) {
    Platform.runLater(() -> txtData.setText(text));
  }

  public void showRecordedData(int[] viewData, int dimesion, int windowSize) {
    Platform.runLater(() -> dataPattern.update(viewData, dimesion, windowSize));
  }

}