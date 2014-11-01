package com.github.thomasfischl.brainintercom.recorder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import com.github.thomasfischl.brainintercom.recorder.recognize.RecognizerPattern;

public class RecognizerPatternModel {
  // private ReadOnlyStringWrapper name = new ReadOnlyStringWrapper();
  private BooleanProperty selected = new SimpleBooleanProperty(false);
  private RecognizerPattern pattern;

  public RecognizerPatternModel(RecognizerPattern pattern) {
    this.pattern = pattern;
    // name.setValue(pattern.getName());
  }

  public String getName() {
    return pattern.getName();
  }

  public RecognizerPattern getPattern() {
    return pattern;
  }

  public boolean isSelected() {
    return selected.get();
  }

  public BooleanProperty selectedProperty() {
    return selected;
  }

}
