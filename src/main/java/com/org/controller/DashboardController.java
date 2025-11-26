package com.org.controller;

import com.org.entity.Account;
import com.org.ui.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {

  @FXML
  private Label welcomeLabel;

  @FXML
  private Label roleLabel;

  @FXML
  private Button logoutButton;

  @FXML
  private Button manageCustomersButton;

  @FXML
  private Button manageCarsButton;

  private final Account account;

  public DashboardController(Account account) {
    this.account = account;
  }

  @FXML
  void initialize() {
    welcomeLabel.setText("Xin chào, " + account.getAccountName());
    roleLabel.setText("Quyền hạn: " + account.getRole());
    logoutButton.setOnAction(event -> SceneNavigator.showLogin());

    // Chỉ hiển thị các nút quản lý cho Admin
    boolean isAdmin = "Admin".equalsIgnoreCase(account.getRole());
    manageCustomersButton.setVisible(isAdmin);
    manageCustomersButton.setManaged(isAdmin);
    manageCarsButton.setVisible(isAdmin);
    manageCarsButton.setManaged(isAdmin);
    
    if (isAdmin) {
      manageCustomersButton.setOnAction(event -> SceneNavigator.showCustomerManagement(account));
      manageCarsButton.setOnAction(event -> SceneNavigator.showCarManagement(account));
    }
  }
}

