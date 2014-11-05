package com.github.thomasfischl.brainintercom.recorder.recognize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

public class RecognizerPatternStore {

  public RecognizerPattern loadPattern(File f) throws FileNotFoundException {
    return new Gson().fromJson(new FileReader(f), RecognizerPattern.class);
  }

  public void storePattern(RecognizerPattern pattern, File f) throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
      bw.write(new Gson().toJson(pattern));
    }
  }

}
