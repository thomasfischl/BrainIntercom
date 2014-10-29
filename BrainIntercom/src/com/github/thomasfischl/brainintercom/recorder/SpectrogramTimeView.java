package com.github.thomasfischl.brainintercom.recorder;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.github.thomasfischl.brainintercom.util.FFTWindowData;

public class SpectrogramTimeView extends Canvas {

  public void update(FFTWindowData[] fftWindowDatas) {
    GraphicsContext gc = getGraphicsContext2D();
    gc.clearRect(0, 0, getWidth(), getHeight());

    double rectWidth = getWidth() / fftWindowDatas.length;
    double rectHeight = getHeight() / fftWindowDatas[0].data.length;

    double widthStart = fftWindowDatas.length * rectWidth;

    for (int row = 0; row < fftWindowDatas.length; row++) {
      for (int i = 0; i < fftWindowDatas[row].data.length; i++) {

        double colorVal = 1 - Math.min(fftWindowDatas[row].data[i] / 100, 1);
        if (fftWindowDatas[row].hasEvent()) {
          gc.setFill(Color.color(colorVal, 0, 0));
        } else {
          gc.setFill(Color.gray(colorVal));
        }
        gc.fillRect(widthStart - row * rectWidth, getHeight() - (i * rectHeight), rectWidth, rectHeight);
      }
    }
  }

}
