package com.github.thomasfischl.brainintercom.analyzer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageGenerator {

  public static void generateWindowImage(File file, int width, int height, int[] data) throws IOException {
    BufferedImage image = new BufferedImage(width, height + 2, BufferedImage.TYPE_INT_RGB);

    Graphics g = image.getGraphics();
    g.setColor(Color.GRAY);
    g.fillRect(0, 0, width, height);

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        switch (data[(height * x) + y]) {
        case 0:
          g.setColor(Color.WHITE);
          break;
        case 1:
          g.setColor(Color.GREEN);
          break;
        case 2:
          g.setColor(Color.YELLOW);
          break;
        case 3:
          g.setColor(Color.ORANGE);
          break;
        case 4:
          g.setColor(Color.RED);
          break;

        default:
          g.setColor(Color.BLACK);
        }
        g.drawRect(x, y, 1, 1);
      }
    }
    ImageIO.write(image, "png", file);
  }

}
