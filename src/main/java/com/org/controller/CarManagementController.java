package com.org.controller;

import com.org.ServiceLocator;
import com.org.entity.Account;
import com.org.entity.Car;
import com.org.entity.CarProducer;
import com.org.service.CarProducerService;
import com.org.service.CarService;
import com.org.ui.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class CarManagementController {

  @FXML
  private TableView<Car> carTable;

  @FXML
  private TableColumn<Car, Long> idColumn;

  @FXML
  private TableColumn<Car, String> nameColumn;

  @FXML
  private TableColumn<Car, Integer> yearColumn;

  @FXML
  private TableColumn<Car, String> colorColumn;

  @FXML
  private TableColumn<Car, String> producerColumn;

  @FXML
  private TableColumn<Car, BigDecimal> priceColumn;

  @FXML
  private TableColumn<Car, String> statusColumn;

  @FXML
  private Button addButton;

  @FXML
  private Button editButton;

  @FXML
  private Button deleteButton;

  @FXML
  private Button changeStatusButton;

  @FXML
  private Button manageProducerButton;

  @FXML
  private Button backButton;

  private final CarService carService;
  private final CarProducerService producerService;
  private final Account currentAccount;
  private final ObservableList<Car> carList;

  public CarManagementController(Account account) {
    this.currentAccount = account;
    this.carService = ServiceLocator.getCarService();
    this.producerService = ServiceLocator.getCarProducerService();
    this.carList = FXCollections.observableArrayList();
  }

  @FXML
  void initialize() {
    setupTable();
    loadCars();
    setupButtons();
  }

  private void setupTable() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("carName"));
    yearColumn.setCellValueFactory(new PropertyValueFactory<>("carModelYear"));
    colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
    producerColumn.setCellValueFactory(cellData -> {
      CarProducer producer = cellData.getValue().getProducer();
      return new javafx.beans.property.SimpleStringProperty(
          producer != null ? producer.getProducerName() : "");
    });
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("rentPrice"));
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

    carTable.setItems(carList);
    carTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }

  private void loadCars() {
    try {
      carList.clear();
      carList.addAll(carService.getAllCars());
    } catch (Exception e) {
      showError("Lỗi", "Không thể tải danh sách xe: " + e.getMessage());
    }
  }

  private void setupButtons() {
    editButton.disableProperty().bind(
        carTable.getSelectionModel().selectedItemProperty().isNull());
    deleteButton.disableProperty().bind(
        carTable.getSelectionModel().selectedItemProperty().isNull());
    changeStatusButton.disableProperty().bind(
        carTable.getSelectionModel().selectedItemProperty().isNull());
  }

  @FXML
  void handleAdd(ActionEvent event) {
    showCarDialog(null);
  }

  @FXML
  void handleEdit(ActionEvent event) {
    Car selected = carTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      showCarDialog(selected);
    }
  }

  @FXML
  void handleDelete(ActionEvent event) {
    Car selected = carTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
      confirmAlert.setTitle("Xác nhận xóa");
      confirmAlert.setHeaderText("Xóa xe");
      confirmAlert.setContentText("Bạn có chắc chắn muốn xóa xe: " + selected.getCarName() + "?");
      
      Optional<ButtonType> result = confirmAlert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        try {
          carService.deleteCar(selected.getId());
          showSuccess("Thành công", "Đã xóa xe thành công");
          loadCars();
        } catch (Exception e) {
          showError("Lỗi", e.getMessage());
        }
      }
    }
  }

  @FXML
  void handleChangeStatus(ActionEvent event) {
    Car selected = carTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      showStatusDialog(selected);
    }
  }

  @FXML
  void handleManageProducer(ActionEvent event) {
    SceneNavigator.showCarProducerManagement(currentAccount);
  }

  @FXML
  void handleBack(ActionEvent event) {
    SceneNavigator.showDashboard(currentAccount);
  }

  private void showCarDialog(Car car) {
    Dialog<Car> dialog = new Dialog<>();
    dialog.setTitle(car == null ? "Thêm xe mới" : "Chỉnh sửa thông tin xe");
    dialog.setHeaderText(car == null ? "Nhập thông tin xe mới" : "Cập nhật thông tin xe");

    ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    // Form fields
    TextField nameField = new TextField();
    nameField.setPromptText("Tên xe");
    Spinner<Integer> yearSpinner = new Spinner<>(2000, 2030, 2024);
    yearSpinner.setEditable(true);
    TextField colorField = new TextField();
    colorField.setPromptText("Màu sắc");
    Spinner<Integer> capacitySpinner = new Spinner<>(1, 20, 5);
    capacitySpinner.setEditable(true);
    TextArea descriptionArea = new TextArea();
    descriptionArea.setPromptText("Mô tả");
    descriptionArea.setPrefRowCount(3);
    DatePicker importDatePicker = new DatePicker();
    importDatePicker.setPromptText("Ngày nhập");
    TextField priceField = new TextField();
    priceField.setPromptText("Giá thuê (VND)");
    ComboBox<String> statusCombo = new ComboBox<>();
    statusCombo.getItems().addAll("Available", "Rented", "Maintenance", "Sold");
    ComboBox<CarProducer> producerCombo = new ComboBox<>();
    producerCombo.setItems(FXCollections.observableArrayList(producerService.getAllProducers()));
    producerCombo.setCellFactory(param -> new ListCell<CarProducer>() {
      @Override
      protected void updateItem(CarProducer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getProducerName());
        }
      }
    });
    producerCombo.setButtonCell(new ListCell<CarProducer>() {
      @Override
      protected void updateItem(CarProducer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getProducerName());
        }
      }
    });

    if (car != null) {
      nameField.setText(car.getCarName());
      yearSpinner.getValueFactory().setValue(car.getCarModelYear());
      colorField.setText(car.getColor());
      capacitySpinner.getValueFactory().setValue(car.getCapacity());
      descriptionArea.setText(car.getDescription());
      importDatePicker.setValue(car.getImportDate());
      priceField.setText(car.getRentPrice().toString());
      statusCombo.setValue(car.getStatus());
      producerCombo.setValue(car.getProducer());
    } else {
      statusCombo.setValue("Available");
    }

    // Layout
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

    grid.add(new Label("Tên xe:"), 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(new Label("Năm sản xuất:"), 0, 1);
    grid.add(yearSpinner, 1, 1);
    grid.add(new Label("Màu sắc:"), 0, 2);
    grid.add(colorField, 1, 2);
    grid.add(new Label("Số chỗ ngồi:"), 0, 3);
    grid.add(capacitySpinner, 1, 3);
    grid.add(new Label("Mô tả:"), 0, 4);
    grid.add(descriptionArea, 1, 4);
    grid.add(new Label("Ngày nhập:"), 0, 5);
    grid.add(importDatePicker, 1, 5);
    grid.add(new Label("Giá thuê:"), 0, 6);
    grid.add(priceField, 1, 6);
    grid.add(new Label("Trạng thái:"), 0, 7);
    grid.add(statusCombo, 1, 7);
    grid.add(new Label("Nhà sản xuất:"), 0, 8);
    grid.add(producerCombo, 1, 8);

    dialog.getDialogPane().setContent(grid);

    // Enable/Disable save button
    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
    saveButton.disableProperty().bind(
        nameField.textProperty().isEmpty()
            .or(colorField.textProperty().isEmpty())
            .or(descriptionArea.textProperty().isEmpty())
            .or(importDatePicker.valueProperty().isNull())
            .or(priceField.textProperty().isEmpty())
            .or(statusCombo.valueProperty().isNull())
            .or(producerCombo.valueProperty().isNull()));

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        try {
          BigDecimal price = new BigDecimal(priceField.getText().trim());
          if (car == null) {
            Car newCar = carService.createCar(
                nameField.getText().trim(),
                yearSpinner.getValue(),
                colorField.getText().trim(),
                capacitySpinner.getValue(),
                descriptionArea.getText().trim(),
                importDatePicker.getValue(),
                price,
                statusCombo.getValue(),
                producerCombo.getValue().getId()
            );
            showSuccess("Thành công", "Đã thêm xe mới thành công");
            return newCar;
          } else {
            Car updatedCar = carService.updateCar(
                car.getId(),
                nameField.getText().trim(),
                yearSpinner.getValue(),
                colorField.getText().trim(),
                capacitySpinner.getValue(),
                descriptionArea.getText().trim(),
                importDatePicker.getValue(),
                price,
                statusCombo.getValue(),
                producerCombo.getValue().getId()
            );
            showSuccess("Thành công", "Đã cập nhật thông tin xe thành công");
            return updatedCar;
          }
        } catch (Exception e) {
          showError("Lỗi", e.getMessage());
          return null;
        }
      }
      return null;
    });

    Optional<Car> result = dialog.showAndWait();
    if (result.isPresent()) {
      loadCars();
    }
  }

  private void showStatusDialog(Car car) {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Chuyển trạng thái xe");
    dialog.setHeaderText("Chuyển trạng thái cho xe: " + car.getCarName());

    ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

    ComboBox<String> statusCombo = new ComboBox<>();
    statusCombo.getItems().addAll("Available", "Rented", "Maintenance", "Sold");
    statusCombo.setValue(car.getStatus());

    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

    grid.add(new Label("Trạng thái mới:"), 0, 0);
    grid.add(statusCombo, 1, 0);

    dialog.getDialogPane().setContent(grid);

    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
    saveButton.disableProperty().bind(statusCombo.valueProperty().isNull());

    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        try {
          carService.updateCarStatus(car.getId(), statusCombo.getValue());
          showSuccess("Thành công", "Đã cập nhật trạng thái xe thành công");
          return statusCombo.getValue();
        } catch (Exception e) {
          showError("Lỗi", e.getMessage());
          return null;
        }
      }
      return null;
    });

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {
      loadCars();
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

