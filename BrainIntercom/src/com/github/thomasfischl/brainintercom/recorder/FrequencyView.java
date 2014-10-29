package com.github.thomasfischl.brainintercom.recorder;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FrequencyView extends Canvas {

  private Color lineColor = Color.BLUE;

  public void setLineColor(Color lineColor) {
    this.lineColor = lineColor;
  }

  public void update(double[] data) {
    GraphicsContext gc = getGraphicsContext2D();

    gc.setStroke(lineColor);
    gc.setLineWidth(1);
    gc.clearRect(0, 0, getWidth(), getHeight());

    double[] xPoints = new double[data.length];
    double[] yPoints = new double[data.length];

    double middleOfHeight = getHeight() / 2;
    double width = getWidth() / data.length;

    double heightFactor = middleOfHeight / 500;

    for (int i = 0; i < data.length; i++) {
      xPoints[i] = i * width;
      yPoints[i] = (data[i] * heightFactor) + middleOfHeight;
    }

    gc.strokePolyline(xPoints, yPoints, data.length - 1);
  }

}
