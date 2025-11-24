package com.org.controller;

import com.org.service.AuthService;
import com.org.service.dto.RegistrationRequest;
import com.org.ui.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

  @FXML
  private TextField usernameField;

  @FXML
  private TextField fullNameField;

  @FXML
  private TextField mobileField;

  @FXML
  private DatePicker birthdayPicker;

  @FXML
  private TextField identityCardField;

  @FXML
  private TextField emailField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private PasswordField confirmPasswordField;

  @FXML
  private Button registerButton;

  private final AuthService authService;

  public RegisterController(AuthService authService) {
    this.authService = authService;
  }

  @FXML
  void initialize() {
    registerButton.disableProperty().bind(
        usernameField.textProperty().isEmpty()
            .or(fullNameField.textProperty().isEmpty())
            .or(mobileField.textProperty().isEmpty())
            .or(identityCardField.textProperty().isEmpty())
            .or(emailField.textProperty().isEmpty())
            .or(passwordField.textProperty().isEmpty())
            .or(confirmPasswordField.textProperty().isEmpty())
            .or(birthdayPicker.valueProperty().isNull()));
  }

  @FXML
  void handleRegister(ActionEvent event) {
    if (!passwordField.getText().equals(confirmPasswordField.getText())) {
      showAlert(Alert.AlertType.WARNING, "Mật khẩu xác nhận không khớp");
      return;
    }
    if (birthdayPicker.getValue() == null) {
      showAlert(Alert.AlertType.WARNING, "Vui lòng chọn ngày sinh");
      return;
    }
    try {
      RegistrationRequest request = new RegistrationRequest(
          usernameField.getText().trim(),
          passwordField.getText(),
          fullNameField.getText().trim(),
          mobileField.getText().trim(),
          birthdayPicker.getValue(),
          identityCardField.getText().trim(),
          emailField.getText().trim()
      );
      authService.registerCustomer(request);
      showAlert(Alert.AlertType.INFORMATION, "Đăng ký thành công. Vui lòng đăng nhập.");
      SceneNavigator.showLogin();
    } catch (IllegalStateException ex) {
      showAlert(Alert.AlertType.ERROR, ex.getMessage());
    }
  }

  @FXML
  void goBack(ActionEvent event) {
    SceneNavigator.showLogin();
  }

  private void showAlert(Alert.AlertType type, String message) {
    Alert alert = new Alert(type);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}

