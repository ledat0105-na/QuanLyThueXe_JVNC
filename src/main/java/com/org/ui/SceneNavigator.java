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
    primaryStage.setTitle("Ứng Dụng Cho Thuê Xe");
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
  public static void showCustomerManagement(Account account) {
    FXMLLoader loader = createLoader("/view/customer_management_view.fxml",
        () -> new com.org.controller.CustomerManagementController(account));
    setScene(loader);
  }
  public static void showCarManagement(Account account) {
    FXMLLoader loader = createLoader("/view/car_management_view.fxml",
        () -> new com.org.controller.CarManagementController(account));
    setScene(loader);
  }
  public static void showCarProducerManagement(Account account) {
    FXMLLoader loader = createLoader("/view/car_producer_management_view.fxml",
        () -> new com.org.controller.CarProducerManagementController(account));
    setScene(loader);
  }
  public static void showCarRentalManagement(Account account) {
    FXMLLoader loader = createLoader("/view/car_rental_management_view.fxml",
        () -> new com.org.controller.CarRentalManagementController(account));
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
