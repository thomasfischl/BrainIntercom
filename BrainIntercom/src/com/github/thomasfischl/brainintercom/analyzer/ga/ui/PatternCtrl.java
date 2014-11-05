package com.github.thomasfischl.brainintercom.analyzer.ga.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import com.github.thomasfischl.brainintercom.analyzer.ga.Configuration;

public class PatternCtrl extends AnchorPane {

  @FXML
  private ImageView image;

  private int factor = 2;

  private boolean fullSize;

  private ColorMapper mapper = new DefaultColorMapper(Configuration.dataRangeSize);

  public PatternCtrl() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PatternCtrl.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    try {
      fxmlLoader.load();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public PatternCtrl(int factor, boolean fullSize) {
    this();
    this.fullSize = fullSize;
    this.factor = factor;
  }

  public PatternCtrl(int factor, boolean fullSize, ColorMapper mapper) {
    this();
    this.fullSize = fullSize;
    this.factor = factor;
    this.mapper = mapper;
  }

  public void setFactor(int factor) {
    this.factor = factor;
  }

  public void setFullSize() {
    this.fullSize = true;
  }

  public void update(int[] data, int dimension, int windowSize) {

    WritableImage wImage = new WritableImage(windowSize * factor, dimension * factor);
    PixelWriter pixelWriter = wImage.getPixelWriter();

    for (int x = 0; x < windowSize; x++) {
      for (int y = 0; y < dimension; y++) {
        Color color = mapper.map(data[(dimension * x) + y]);

        for (int xfac = 0; xfac < factor; xfac++) {
          for (int yfac = 0; yfac < factor; yfac++) {
            pixelWriter.setColor(x * factor + xfac, y * factor + yfac, color);
          }
        }
      }
    }

    image.setImage(wImage);
    image.setFitWidth(wImage.getWidth());
    image.setFitHeight(wImage.getHeight());

    if (fullSize) {
      setMinHeight(wImage.getHeight() + 25);
      setMinWidth(wImage.getWidth() + 25);
    } else {
      setPrefHeight(wImage.getHeight() + 25);
      setPrefWidth(wImage.getWidth() + 25);
    }

  }

}
