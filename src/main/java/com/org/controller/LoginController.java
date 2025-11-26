package com.org.controller;
import com.org.entity.Account;
import com.org.service.AuthService;
import com.org.ui.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
public class LoginController {
  @FXML
  private TextField usernameField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private Button loginButton;
  private final AuthService authService;
  public LoginController(AuthService authService) {
    this.authService = authService;
  }
  @FXML
  void initialize() {
    loginButton.disableProperty().bind(
        usernameField.textProperty().isEmpty()
            .or(passwordField.textProperty().isEmpty()));
  }
  @FXML
  void handleLogin(ActionEvent event) {
    authService.login(usernameField.getText().trim(), passwordField.getText())
        .ifPresentOrElse(this::navigateByRole, () -> showError("Sai tên đăng nhập hoặc mật khẩu"));
  }
  @FXML
  void openRegister(ActionEvent event) {
    SceneNavigator.showRegister();
  }
  private void navigateByRole(Account account) {
    SceneNavigator.showDashboard(account);
  }
  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setHeaderText("Đăng nhập thất bại");
    alert.setContentText(message);
    alert.showAndWait();
  }
}
