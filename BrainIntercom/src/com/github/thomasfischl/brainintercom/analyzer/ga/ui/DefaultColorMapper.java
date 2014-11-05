package com.github.thomasfischl.brainintercom.analyzer.ga.ui;

import javafx.scene.paint.Color;

public class DefaultColorMapper implements ColorMapper {

  private Color[] colors;

  public DefaultColorMapper(int size) {
    colors = new Color[size];
    colors[0] = Color.WHITE;

    double interval = (double) 180 / size;
    for (int i = 1; i < size; i++) {
      colors[i] = Color.hsb(interval * i, 0.85, 1.0);
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
