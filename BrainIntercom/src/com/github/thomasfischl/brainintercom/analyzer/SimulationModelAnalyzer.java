package com.github.thomasfischl.brainintercom.analyzer;

import java.io.File;
import java.io.IOException;

import com.github.thomasfischl.brainintercom.analyzer.SimulationModel.SimulationModelWindow;
import com.github.thomasfischl.brainintercom.util.FileUtil;

public class SimulationModelAnalyzer {

  public static void main(String[] args) throws IOException {
    // SimulationModel model = new SimulationModel(50, 30, new File("./data/MicrophoneDataProvider-1414498984805.csv"), 25);
    SimulationModel model = new SimulationModel(20, 30, new File("./data/ArduinoDataProvider-1414530526911.csv"), 25);
    System.out.println("Number of Windwos: " + model.getWindows().size());

    File folder = new File("C:/tmp/analyzer/images");
    FileUtil.deleteFolder(folder);
    folder.mkdirs();

    long time = 0;

    for (SimulationModelWindow window : model.getWindows()) {
      ImageGenerator.generateWindowImage(new File(folder, String.format("%06d.png", time)), window.getWindowSize(), window.getDimension(),
          window.data);
      time++;

      if (time % 100 == 0) {
        System.out.println("Time: " + time);
      }
    }
  }
}
