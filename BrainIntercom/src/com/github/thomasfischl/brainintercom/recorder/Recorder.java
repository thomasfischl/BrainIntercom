package com.github.thomasfischl.brainintercom.recorder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import javax.sound.sampled.LineUnavailableException;

import com.github.thomasfischl.brainintercom.provider.ArduinoDataProvider;
import com.github.thomasfischl.brainintercom.provider.MicrophoneDataProvider;
import com.github.thomasfischl.brainintercom.provider.RecFileDataProvider;
import com.github.thomasfischl.brainintercom.provider.SinusDataProvider;

public class Recorder extends AnchorPane {

  @FXML
  private Button btnStart;
  @FXML
  private ComboBox<String> cbProvider;
  @FXML
  private HBox box1;
  @FXML
  private HBox box2;
  @FXML
  private Slider sMultiplier;
  @FXML
  private ComboBox<Double> cbMultiplier;
  @FXML
  private Button btnRecordEvent;

  private SpectrogramView spectrogram1 = new SpectrogramView();
  private SpectrogramView spectrogram2 = new SpectrogramView();
  private FrequencyView frequencyView = new FrequencyView();
  private SpectrogramTimeView spectrogramTimeView = new SpectrogramTimeView();

  private final DataEngine recorder;
  private final ScheduledExecutorService pool;

  private boolean recording = false;

  public Recorder() throws LineUnavailableException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Recorder.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    box1.getChildren().clear();

    frequencyView.setWidth(600);
    frequencyView.setHeight(400);

    spectrogram1.setWidth(600);
    spectrogram1.setHeight(200);

    spectrogram2.setWidth(600);
    spectrogram2.setHeight(200);

    spectrogramTimeView.setWidth(600);
    spectrogramTimeView.setHeight(200);

    box1.getChildren().addAll(spectrogramTimeView, spectrogram1);
    box2.getChildren().addAll(frequencyView, spectrogram2);

    recorder = new DataEngine();

    pool = Executors.newScheduledThreadPool(2);
    pool.scheduleAtFixedRate(() -> Platform.runLater(() -> updateView()), 0, 50, TimeUnit.MILLISECONDS);
    pool.execute(() -> recorder.record());

    cbProvider.getItems().addAll("Arduino", "Microphone", "Sinus");
    cbProvider.setOnAction(e -> {
      updateProvider();
    });

    for (File f : new File("./data").listFiles()) {
      if (f.getName().endsWith(".csv")) {
        cbProvider.getItems().add("RecFile - " + f.getName());
      }
    }

    sMultiplier.setValue(1);
    sMultiplier.valueProperty().addListener(e -> updateMultiplier(sMultiplier));
    cbMultiplier.setOnAction(e -> updateMultiplier(cbMultiplier));
  }

  private void updateMultiplier(Control ctrl) {
    if (ctrl == sMultiplier) {
      double value = sMultiplier.getValue();
      recorder.setMultiplier(value);
      cbMultiplier.setValue(value);
    }

    if (ctrl == cbMultiplier) {
      try {
        double value = cbMultiplier.getValue();
        recorder.setMultiplier(value);
        sMultiplier.setValue(value);
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
  }

  private void updateProvider() {
    String provider = cbProvider.getSelectionModel().getSelectedItem();
    switch (provider) {
    case "Arduino":
      recorder.setProvider(new ArduinoDataProvider());
      break;
    case "Microphone":
      recorder.setProvider(new MicrophoneDataProvider());
      break;
    case "Sinus":
      recorder.setProvider(new SinusDataProvider());
      break;
    default:

      if (provider.startsWith("RecFile - ")) {
        recorder.setProvider(new RecFileDataProvider(new File("./data", provider.substring("RecFile - ".length()))));
      } else {
        recorder.setProvider(null);
      }
    }
  }

  @FXML
  private void start(ActionEvent e) {

    if (recording) {
      recorder.recordToFile(false);
      recording = false;
      btnStart.setText("Start");
    } else {
      btnStart.setText("Stop");
      recorder.recordToFile(true);
      recording = true;
    }

  }

  private void updateView() {
    frequencyView.update(recorder.getWindowData(600));

    spectrogram1.update(recorder.getFFT(100));
    spectrogram2.update(recorder.getReduceFFT());
    spectrogramTimeView.update(recorder.getReducedFFTWindowData(100));

    if (recorder.isEventActive()) {
      btnRecordEvent.setText("...");
    } else {
      btnRecordEvent.setText("Event");
    }

  }

  @FXML
  private void recordEvent(ActionEvent e) {
    recorder.setEvent("noise", 1);
  }

  public void stop() {
    recorder.stop();
    pool.shutdownNow();
  }
}
