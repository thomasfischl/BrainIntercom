package com.github.thomasfischl.brainintercom.analyzer.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GAViewLauncher extends Application {

  private GAView view;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    view = new GAView();
    primaryStage.setScene(new Scene(view, 1200, 700));
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    view.stop();
    super.stop();
  }

}
