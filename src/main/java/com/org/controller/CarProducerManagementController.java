package com.org.controller;

import com.org.ServiceLocator;
import com.org.entity.Account;
import com.org.entity.CarProducer;
import com.org.service.CarProducerService;
import com.org.ui.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class CarProducerManagementController {

  @FXML
  private TableView<CarProducer> producerTable;

  @FXML
  private TableColumn<CarProducer, Long> idColumn;

  @FXML
  private TableColumn<CarProducer, String> nameColumn;

  @FXML
  private TableColumn<CarProducer, String> addressColumn;

  @FXML
  private TableColumn<CarProducer, String> countryColumn;

  @FXML
  private Button addButton;

  @FXML
  private Button editButton;

  @FXML
  private Button deleteButton;

  @FXML
  private Button backButton;

  private final CarProducerService producerService;
  private final Account currentAccount;
  private final ObservableList<CarProducer> producerList;

  public CarProducerManagementController(Account account) {
    this.currentAccount = account;
    this.producerService = ServiceLocator.getCarProducerService();
    this.producerList = FXCollections.observableArrayList();
  }

  @FXML
  void initialize() {
    setupTable();
    loadProducers();
    setupButtons();
  }

  private void setupTable() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("producerName"));
    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
    countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));

    producerTable.setItems(producerList);
    producerTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }

  private void loadProducers() {
    try {
      producerList.clear();
      producerList.addAll(producerService.getAllProducers());
    } catch (Exception e) {
      showError("Lỗi", "Không thể tải danh sách nhà sản xuất: " + e.getMessage());
    }
  }

  private void setupButtons() {
    editButton.disableProperty().bind(
        producerTable.getSelectionModel().selectedItemProperty().isNull());
    deleteButton.disableProperty().bind(
        producerTable.getSelectionModel().selectedItemProperty().isNull());
  }

  @FXML
  void handleAdd(ActionEvent event) {
    showProducerDialog(null);
  }

  @FXML
  void handleEdit(ActionEvent event) {
    CarProducer selected = producerTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      showProducerDialog(selected);
    }
  }

  @FXML
  void handleDelete(ActionEvent event) {
    CarProducer selected = producerTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confirmAlert.setTitle("Xác nhận xóa");
      confirmAlert.setHeaderText("Xóa nhà sản xuất");
      confirmAlert.setContentText("Bạn có chắc chắn muốn xóa nhà sản xuất: " + selected.getProducerName() + "?");
      
      Optional<ButtonType> result = confirmAlert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
          producerService.deleteProducer(selected.getId());
          showSuccess("Thành công", "Đã xóa nhà sản xuất thành công");
          loadProducers();
        } catch (Exception e) {
          showError("Lỗi", e.getMessage());
        }
      }
    }
  }

  @FXML
  void handleBack(ActionEvent event) {
    SceneNavigator.showCarManagement(currentAccount);
  }

  private void showProducerDialog(CarProducer producer) {
    Dialog<CarProducer> dialog = new Dialog<>();
    dialog.setTitle(producer == null ? "Thêm nhà sản xuất mới" : "Chỉnh sửa nhà sản xuất");
    dialog.setHeaderText(producer == null ? "Nhập thông tin nhà sản xuất mới" : "Cập nhật thông tin nhà sản xuất");

    ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // Form fields
    TextField nameField = new TextField();
    nameField.setPromptText("Tên nhà sản xuất");
    TextField addressField = new TextField();
    addressField.setPromptText("Địa chỉ");
    TextField countryField = new TextField();
    countryField.setPromptText("Quốc gia");

    if (producer != null) {
      nameField.setText(producer.getProducerName());
      addressField.setText(producer.getAddress());
      countryField.setText(producer.getCountry());
    }

    // Layout
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

    grid.add(new Label("Tên nhà sản xuất:"), 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(new Label("Địa chỉ:"), 0, 1);
    grid.add(addressField, 1, 1);
    grid.add(new Label("Quốc gia:"), 0, 2);
    grid.add(countryField, 1, 2);

    dialog.getDialogPane().setContent(grid);

    // Enable/Disable save button
    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
    saveButton.disableProperty().bind(
        nameField.textProperty().isEmpty()
            .or(addressField.textProperty().isEmpty())
            .or(countryField.textProperty().isEmpty()));

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        try {
          if (producer == null) {
            CarProducer newProducer = producerService.createProducer(
                nameField.getText().trim(),
                addressField.getText().trim(),
                countryField.getText().trim()
            );
            showSuccess("Thành công", "Đã thêm nhà sản xuất mới thành công");
            return newProducer;
          } else {
            CarProducer updatedProducer = producerService.updateProducer(
                producer.getId(),
                nameField.getText().trim(),
                addressField.getText().trim(),
                countryField.getText().trim()
            );
            showSuccess("Thành công", "Đã cập nhật thông tin nhà sản xuất thành công");
            return updatedProducer;
          }
        } catch (Exception e) {
          showError("Lỗi", e.getMessage());
          return null;
        }
      }
      return null;
    });

    Optional<CarProducer> result = dialog.showAndWait();
    if (result.isPresent()) {
      loadProducers();
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

