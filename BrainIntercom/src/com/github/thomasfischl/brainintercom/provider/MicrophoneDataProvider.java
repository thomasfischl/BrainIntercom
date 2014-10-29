package com.github.thomasfischl.brainintercom.provider;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneDataProvider implements DataProvider {

  private TargetDataLine audioInputStream;

  public MicrophoneDataProvider() {
    try {
      start();
    } catch (LineUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  public void start() throws LineUnavailableException {
    AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    audioInputStream = AudioSystem.getTargetDataLine(format);
    audioInputStream.open();
    audioInputStream.start();
  }

  @Override
  public double read() {
    try {
      int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
      if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
        bytesPerFrame = 1;
        System.out.println("Unspecified amount of frames in file");
      }

      byte[] audioBytes = new byte[2];
      if ((audioInputStream.read(audioBytes, 0, audioBytes.length)) != -1) {
        ByteBuffer buffer = ByteBuffer.wrap(audioBytes);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return buffer.getShort();
      }
    } catch (Exception e) {
      System.out.println("Error! Audio not compatible");
    }
    return DataProvider.NO_DATA;
  }

  @Override
  public void stop() {
    if (audioInputStream != null) {
      audioInputStream.close();
    }

  }
}
