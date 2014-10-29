package com.github.thomasfischl.brainintercom.recorder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RecorderLauncher extends Application {

	private Recorder recorder;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		recorder = new Recorder();
		primaryStage.setScene(new Scene(recorder, 1200, 700));
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		recorder.stop();
		super.stop();
	}

}
