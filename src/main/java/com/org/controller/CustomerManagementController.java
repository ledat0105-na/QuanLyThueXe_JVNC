package com.org.controller;

import com.org.ServiceLocator;
import com.org.entity.Account;
import com.org.entity.Customer;
import com.org.service.CustomerService;
import com.org.ui.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class CustomerManagementController {

  @FXML
  private TableView<Customer> customerTable;

  @FXML
  private TableColumn<Customer, Long> idColumn;

  @FXML
  private TableColumn<Customer, String> nameColumn;

  @FXML
  private TableColumn<Customer, String> mobileColumn;

  @FXML
  private TableColumn<Customer, String> emailColumn;

  @FXML
  private TableColumn<Customer, String> identityCardColumn;

  @FXML
  private Button addButton;

  @FXML
  private Button editButton;

  @FXML
  private Button deleteButton;

  @FXML
  private Button backButton;

  private final CustomerService customerService;
  private final Account currentAccount;
  private final ObservableList<Customer> customerList;

  public CustomerManagementController(Account account) {
    this.currentAccount = account;
    this.customerService = ServiceLocator.getCustomerService();
    this.customerList = FXCollections.observableArrayList();
  }

  @FXML
  void initialize() {
    setupTable();
    loadCustomers();
    setupButtons();
  }

  private void setupTable() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
    mobileColumn.setCellValueFactory(new PropertyValueFactory<>("mobile"));
    emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
    identityCardColumn.setCellValueFactory(new PropertyValueFactory<>("identityCard"));

    customerTable.setItems(customerList);
    customerTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }

  private void loadCustomers() {
    try {
      customerList.clear();
      customerList.addAll(customerService.getAllCustomers());
    } catch (Exception e) {
      showError("Lỗi", "Không thể tải danh sách khách hàng: " + e.getMessage());
    }
  }

  private void setupButtons() {
    editButton.disableProperty().bind(
        customerTable.getSelectionModel().selectedItemProperty().isNull());
    deleteButton.disableProperty().bind(
        customerTable.getSelectionModel().selectedItemProperty().isNull());
  }

  @FXML
  void handleAdd(ActionEvent event) {
    showCustomerDialog(null);
  }

  @FXML
  void handleEdit(ActionEvent event) {
    Customer selected = customerTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      showCustomerDialog(selected);
    }
  }

  @FXML
  void handleDelete(ActionEvent event) {
    Customer selected = customerTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confirmAlert.setTitle("Xác nhận xóa");
      confirmAlert.setHeaderText("Xóa khách hàng");
      confirmAlert.setContentText("Bạn có chắc chắn muốn xóa khách hàng: " + selected.getCustomerName() + "?");
      
      Optional<ButtonType> result = confirmAlert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
          customerService.deleteCustomer(selected.getId());
          showSuccess("Thành công", "Đã xóa khách hàng thành công");
          loadCustomers();
        } catch (Exception e) {
          showError("Lỗi", "Không thể xóa khách hàng: " + e.getMessage());
        }
      }
    }
  }

  @FXML
  void handleBack(ActionEvent event) {
    SceneNavigator.showDashboard(currentAccount);
  }

  private void showCustomerDialog(Customer customer) {
    Dialog<Customer> dialog = new Dialog<>();
    dialog.setTitle(customer == null ? "Thêm khách hàng mới" : "Chỉnh sửa khách hàng");
    dialog.setHeaderText(customer == null ? "Nhập thông tin khách hàng mới" : "Cập nhật thông tin khách hàng");

    ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // Form fields
    TextField nameField = new TextField();
    nameField.setPromptText("Tên khách hàng");
    TextField mobileField = new TextField();
    mobileField.setPromptText("Số điện thoại");
    DatePicker birthdayPicker = new DatePicker();
    birthdayPicker.setPromptText("Ngày sinh");
    TextField identityCardField = new TextField();
    identityCardField.setPromptText("CMND/CCCD");
    TextField emailField = new TextField();
    emailField.setPromptText("Email");
    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("Mật khẩu");

    if (customer != null) {
      nameField.setText(customer.getCustomerName());
      mobileField.setText(customer.getMobile());
      birthdayPicker.setValue(customer.getBirthday());
      identityCardField.setText(customer.getIdentityCard());
      emailField.setText(customer.getEmail());
      passwordField.setDisable(true);
      passwordField.setVisible(false);
    }

    // Layout
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

    grid.add(new Label("Tên khách hàng:"), 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(new Label("Số điện thoại:"), 0, 1);
    grid.add(mobileField, 1, 1);
    grid.add(new Label("Ngày sinh:"), 0, 2);
    grid.add(birthdayPicker, 1, 2);
    grid.add(new Label("CMND/CCCD:"), 0, 3);
    grid.add(identityCardField, 1, 3);
    grid.add(new Label("Email:"), 0, 4);
    grid.add(emailField, 1, 4);
    if (customer == null) {
      grid.add(new Label("Mật khẩu:"), 0, 5);
      grid.add(passwordField, 1, 5);
    }

    dialog.getDialogPane().setContent(grid);

    // Enable/Disable save button
    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
    if (customer == null) {
      saveButton.disableProperty().bind(
          nameField.textProperty().isEmpty()
              .or(mobileField.textProperty().isEmpty())
              .or(birthdayPicker.valueProperty().isNull())
              .or(identityCardField.textProperty().isEmpty())
              .or(emailField.textProperty().isEmpty())
              .or(passwordField.textProperty().isEmpty()));
    } else {
      saveButton.disableProperty().bind(
          nameField.textProperty().isEmpty()
              .or(mobileField.textProperty().isEmpty())
              .or(birthdayPicker.valueProperty().isNull())
              .or(identityCardField.textProperty().isEmpty())
              .or(emailField.textProperty().isEmpty()));
    }

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        try {
          if (customer == null) {
            Customer newCustomer = customerService.createCustomer(
                nameField.getText().trim(),
                mobileField.getText().trim(),
                birthdayPicker.getValue(),
                identityCardField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText()
            );
            showSuccess("Thành công", "Đã thêm khách hàng mới thành công");
            return newCustomer;
          } else {
            Customer updatedCustomer = customerService.updateCustomer(
                customer.getId(),
                nameField.getText().trim(),
                mobileField.getText().trim(),
                birthdayPicker.getValue(),
                identityCardField.getText().trim(),
                emailField.getText().trim()
            );
            showSuccess("Thành công", "Đã cập nhật thông tin khách hàng thành công");
            return updatedCustomer;
          }
        } catch (Exception e) {
          showError("Lỗi", e.getMessage());
          return null;
        }
      }
      return null;
    });

    Optional<Customer> result = dialog.showAndWait();
    if (result.isPresent()) {
      loadCustomers();
    }
  }

  private void showError(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void showSuccess(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}

