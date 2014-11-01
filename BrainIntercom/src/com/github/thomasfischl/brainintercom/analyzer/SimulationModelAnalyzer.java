package com.github.thomasfischl.brainintercom.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import com.github.thomasfischl.brainintercom.analyzer.SimulationModel.SimulationModelWindow;
import com.github.thomasfischl.brainintercom.util.FileUtil;

public class SimulationModelAnalyzer {

  public static void main(String[] args) throws IOException {
    // SimulationModel model = new SimulationModel(50, 30, new File("./data/MicrophoneDataProvider-1414498984805.csv"), 25);
    // SimulationModel model = new SimulationModel(20, 30, new File("./data/ArduinoDataProvider-1.csv"), 25);
    SimulationModel model = new SimulationModel(20, 30, new File("./data/MicrophoneDataProvider-long-1.csv"), 25);
    System.out.println("Number of Windwos: " + model.getWindows().size());

    File folder = new File("C:/tmp/analyzer/images");
    File posfolder = new File("C:/tmp/analyzer/images/pos");
    File negfolder = new File("C:/tmp/analyzer/images/neg");
    FileUtil.deleteFolder(folder);
    folder.mkdirs();
    posfolder.mkdirs();
    negfolder.mkdirs();

    long time = 0;

    for (SimulationModelWindow window : model.getWindows()) {
      ImageGenerator.generateWindowImage(new File(folder, String.format("%06d.png", time)), window.getWindowSize(), window.getDimension(),
          window.data);

      time++;

      if (time % 1000 == 0) {
        System.out.println("Time: " + time);
      }
    }

    for (Entry<Integer, List<SimulationModelWindow>> entry : model.getPositiveExamples().entrySet()) {
      File f1 = new File(posfolder, entry.getKey() + "_");
      FileUtil.deleteFolder(f1);
      f1.mkdirs();

      time = 0;

      for (SimulationModelWindow window : entry.getValue()) {
        ImageGenerator.generateWindowImage(new File(f1, String.format("%06d.png", time)), window.getWindowSize(), window.getDimension(),
            window.data);
        time++;
      }
    }
  }
}
