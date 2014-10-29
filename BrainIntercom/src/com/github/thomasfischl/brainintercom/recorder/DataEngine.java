package com.github.thomasfischl.brainintercom.recorder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import com.github.thomasfischl.brainintercom.provider.DataProvider;
import com.github.thomasfischl.brainintercom.util.FFTWindowData;
import com.github.thomasfischl.brainintercom.util.RealDoubleFFT;

public class DataEngine {

  private final LinkedList<Double> data = new LinkedList<Double>();
  private final LinkedList<FFTWindowData> fftWindowData = new LinkedList<FFTWindowData>();

  private final int size = 10000;
  private final int windowSize = 10000;

  private DataProvider dataProvider;

  private Object monitor = new Object();
  private double multiplier = 1;
  private BufferedWriter recFileWriter;

  private long time = 0;

  private String event = "";
  private int eventCounter = 0;

  public void record() {
    while (true) {
      if (dataProvider == null) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          break;
        }
        continue;
      }
      double val = dataProvider.read();

      synchronized (monitor) {
        if (val != DataProvider.NO_DATA) {
          double value = val * multiplier;
          value = Math.max(-500, Math.min(500, value));
          data.add(value);

          time++;
        }

        double[] vals = getReduceFFT();

        fftWindowData.add(new FFTWindowData(vals, hasEvent()));
        if (fftWindowData.size() > windowSize) {
          fftWindowData.removeFirst();
        }

        if (recFileWriter != null) {
          try {
            recFileWriter.write(time + ";" + data.getLast() + ";" + event + ";");

            FFTWindowData fft = fftWindowData.getLast();
            for (int i = 0; i < fft.getLength(); i++) {
              recFileWriter.write(fft.data[i] + ";");
            }

            recFileWriter.newLine();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }

        if (data.size() > size) {
          data.removeFirst();
        }

        if (hasEvent()) {
          eventCounter--;
          if (event.isEmpty()) {
            event = "event";
          }
        } else {
          event = "";
        }

        try {
          Thread.sleep(2);
        } catch (InterruptedException e) {
          break;
        }

      }
    }
  }

  public double[] getWindowData(int size) {
    synchronized (monitor) {
      double[] result = new double[size];
      if (data.size() > size + 1) {
        int startIndex = data.size() - size;
        for (int i = 0; i < size; i++) {
          result[i] = data.get(startIndex + i);
        }
      }
      return result;
    }
  }

  public FFTWindowData[] getFFTWindowData(int size) {
    synchronized (monitor) {
      FFTWindowData[] result = new FFTWindowData[size];
      if (fftWindowData.size() > size + 1) {
        int startIndex = fftWindowData.size() - size;
        for (int i = 0; i < size; i++) {
          result[i] = fftWindowData.get(startIndex + i);
        }
      }

      for (int i = 0; i < size; i++) {
        if (result[i] == null) {
          result[i] = new FFTWindowData();
        }
      }
      return result;
    }
  }

  public FFTWindowData[] getReducedFFTWindowData(int size) {
    synchronized (monitor) {
      FFTWindowData[] result = new FFTWindowData[size];

      int factor = 10;

      int windowCount = factor * size;

      if (fftWindowData.size() > windowCount + 1) {
        int startIndex = fftWindowData.size() - windowCount;

        int rowSize = fftWindowData.get(0).data.length;
        int idx = 0;
        while (startIndex < fftWindowData.size()) {
          result[idx] = new FFTWindowData(rowSize, false);
          for (int i = 0; i < rowSize; i++) {
            result[idx].data[i] = 0;
          }

          boolean hasEvent = false;
          for (int max = startIndex + factor; startIndex < max; startIndex++) {
            double[] d = fftWindowData.get(startIndex).data;
            hasEvent = hasEvent || fftWindowData.get(startIndex).hasEvent();

            for (int i = 0; i < d.length; i++) {
              result[idx].data[i] += d[i];
            }
          }
          if (hasEvent) {
            result[idx].setHasEvent();
          }

          for (int i = 0; i < rowSize; i++) {
            result[idx].data[i] = result[idx].data[i] / factor;
          }
          idx++;
        }

      }

      for (int i = 0; i < size; i++) {
        if (result[i] == null) {
          result[i] = new FFTWindowData();
        }
      }
      return result;
    }
  }

  public double[] getFFT(int size) {
    double[] val;
    synchronized (monitor) {
      val = getWindowData(size);
    }
    int FFT = val.length;

    RealDoubleFFT fft = new RealDoubleFFT(FFT);
    fft.ft(val);

    for (int i = 0; i < FFT; i++) {
      val[i] = Math.abs(val[i] / 100);
    }

    return val;
  }

  public double[] getReduceFFT() {
    double[] data = getFFT(300);
    double[] reduceData = new double[data.length / 10 + 1];

    int idx = 0;
    int x = 0;

    while (idx < data.length) {
      int sum = 0;
      int count = 0;

      for (int max = Math.min(idx + 10, data.length); idx < max; idx++) {
        sum += data[idx];
        count++;
      }
      reduceData[x++] = sum / count;
    }
    return reduceData;
  }

  public void setProvider(DataProvider dataProvider) {
    this.dataProvider = dataProvider;
    time = 0;
  }

  public void stop() {
    if (dataProvider != null) {
      dataProvider.stop();
    }
  }

  public void recordToFile(boolean start) {
    if (start) {
      File recFile = new File("./data", dataProvider.getClass().getSimpleName() + "-" + System.currentTimeMillis() + ".csv");
      recFile.getParentFile().mkdirs();
      try {
        recFileWriter = new BufferedWriter(new FileWriter(recFile));
      } catch (IOException e) {
        System.err.println("Error:" + e.getMessage());
      }
    } else {
      if (recFileWriter != null) {
        try {
          recFileWriter.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        recFileWriter = null;
      }
    }
  }

  public void setEvent(String event, int duration) {
    this.event = event;
    eventCounter = duration * 200;
  }

  public boolean isEventActive() {
    return eventCounter > 0;
  }

  public void setMultiplier(double multiplier) {
    this.multiplier = multiplier;
  }

  public boolean hasEvent() {
    return eventCounter > 0 || dataProvider.hasEvent();
  }
}
