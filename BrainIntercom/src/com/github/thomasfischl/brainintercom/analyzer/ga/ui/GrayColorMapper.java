package com.github.thomasfischl.brainintercom.analyzer.ga.ui;

import javafx.scene.paint.Color;

public class GrayColorMapper implements ColorMapper {

  private Color[] colors;

  public GrayColorMapper(int size) {
    colors = new Color[size];
    colors[0] = Color.WHITE;

    double interval = (double) 1 / size;
    for (int i = 1; i < size; i++) {
      colors[i] = Color.gray(interval * i);
    }
  }

  @Override
  public Color map(int val) {
    if (val < colors.length) {
      return colors[val];
    }
    return Color.BLACK;
  }

}
