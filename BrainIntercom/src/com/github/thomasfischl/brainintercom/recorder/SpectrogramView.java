package com.github.thomasfischl.brainintercom.recorder;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpectrogramView extends Canvas {

	private Color lineColor = Color.BLUE;

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public void update(double[] data) {
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFill(lineColor);
		gc.setLineWidth(1);
		gc.clearRect(0, 0, getWidth(), getHeight());

		double rectWidth = getWidth() / data.length;
		for (int i = 0; i < data.length; i++) {
			gc.fillRect(i * rectWidth,
					Math.max(getHeight() - Math.max(data[i], 1), 0),
					rectWidth - 1, getHeight());
		}
	}

}
