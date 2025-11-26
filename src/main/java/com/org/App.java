package com.org;
import com.org.ui.SceneNavigator;
import javafx.application.Application;
import javafx.stage.Stage;
public class App extends Application {
  @Override
  public void start(Stage primaryStage) {
    SceneNavigator.init(primaryStage);
    SceneNavigator.showLogin();
  }
  public static void main(String[] args) {
    launch(args);
  }
}
