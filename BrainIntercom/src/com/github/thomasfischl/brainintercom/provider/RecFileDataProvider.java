package com.github.thomasfischl.brainintercom.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RecFileDataProvider implements DataProvider {

  private File recFile;
  private BufferedReader br;

  private boolean hasEvent = false;

  public RecFileDataProvider(File recFile) {
    this.recFile = recFile;
    try {
      start();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void start() throws Exception {
    br = new BufferedReader(new FileReader(recFile));
  }

  @Override
  public double read() {
    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    try {
      String line = br.readLine();
      if (line == null) {
        return DataProvider.NO_DATA;
      }

      String[] parts = line.split(";");

      if (parts.length >= 3) {
        hasEvent = !parts[2].trim().isEmpty();
      } else {
        hasEvent = false;
      }

      return Double.parseDouble(parts[1]);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return DataProvider.NO_DATA;
  }

  @Override
  public void stop() {
    if (br != null) {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  public boolean hasEvent() {
    return hasEvent;
  }
}
