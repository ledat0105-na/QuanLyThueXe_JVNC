package com.org.ui;

import com.org.App;
import com.org.ServiceLocator;
import com.org.controller.DashboardController;
import com.org.controller.LoginController;
import com.org.controller.RegisterController;
import com.org.entity.Account;
import com.org.service.AuthService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

public final class SceneNavigator {

  private static Stage primaryStage;
  private static final AuthService AUTH_SERVICE = ServiceLocator.getAuthService();

  private SceneNavigator() {
  }

  public static void init(Stage stage) {
    primaryStage = stage;
    primaryStage.setTitle("FU Car Renting System");
  }

  public static void showLogin() {
    loadScene("/view/login_view.fxml",
        () -> new LoginController(AUTH_SERVICE));
  }

  public static void showRegister() {
    loadScene("/view/register_view.fxml",
        () -> new RegisterController(AUTH_SERVICE));
  }

  public static void showDashboard(Account account) {
    FXMLLoader loader = createLoader("/view/dashboard_view.fxml",
        () -> new DashboardController(account));
    setScene(loader);
  }

  private static void loadScene(String resource, Supplier<Object> controllerSupplier) {
    FXMLLoader loader = createLoader(resource, controllerSupplier);
    setScene(loader);
  }

  private static FXMLLoader createLoader(String resource, Supplier<Object> controllerSupplier) {
    FXMLLoader loader = new FXMLLoader(App.class.getResource(resource));
    loader.setControllerFactory(param -> controllerSupplier.get());
    return loader;
  }

  private static void setScene(FXMLLoader loader) {
    try {
      Parent parent = loader.load();
      Scene scene = new Scene(parent);
      Objects.requireNonNull(primaryStage, "Navigator chưa được khởi tạo");
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException e) {
      throw new IllegalStateException("Không thể tải giao diện: " + e.getMessage(), e);
    }
  }
}

