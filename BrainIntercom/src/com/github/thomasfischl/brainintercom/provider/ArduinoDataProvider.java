package com.github.thomasfischl.brainintercom.provider;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;

public class ArduinoDataProvider implements DataProvider {

  private InputStream inputStream;
  private CommPort port;

  public static void main(String[] args) throws Exception {
    ArduinoDataProvider provider = new ArduinoDataProvider();

    while (true) {
      System.out.println(provider.read());
    }
  }

  public ArduinoDataProvider() {
    CommPortIdentifier portIdentifier;
    try {
      portIdentifier = CommPortIdentifier.getPortIdentifier("COM5");
      port = portIdentifier.open("Java", 9600);

      ((SerialPort) port).setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

      inputStream = port.getInputStream();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public double read() {

    try {
      StringBuffer sb = new StringBuffer();
      while (true) {// inputStream.available() > 0) {
        int read = inputStream.read();
        if (read == 13) {
          break;
        }
        if (read >= 48 && read <= 57) {
          sb.append((char) read);
        }
      }
      return Double.parseDouble(sb.toString()) - 200;
    } catch (Exception e) {
      // e.printStackTrace();
    }
    return DataProvider.NO_DATA;
  }

  @Override
  public void stop() {
    if (port != null) {
      port.close();
    }
  }

}
