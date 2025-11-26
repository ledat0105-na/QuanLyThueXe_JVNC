package com.org.controller;
import com.org.ServiceLocator;
import com.org.entity.Account;
import com.org.entity.Car;
import com.org.entity.CarRental;
import com.org.entity.Customer;
import com.org.service.CarRentalService;
import com.org.service.CarService;
import com.org.service.CustomerService;
import com.org.ui.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SpinnerValueFactory;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
public class CarRentalManagementController {
  @FXML
  private TableView<CarRental> rentalTable;
  @FXML
  private TableColumn<CarRental, String> sttColumn;
  @FXML
  private TableColumn<CarRental, String> customerNameColumn;
  @FXML
  private TableColumn<CarRental, String> carNameColumn;
  @FXML
  private TableColumn<CarRental, String> pickupDateColumn;
  @FXML
  private TableColumn<CarRental, String> returnDateColumn;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.ITALIAN));
  @FXML
  private TableColumn<CarRental, String> rentPriceColumn;
  @FXML
  private TableColumn<CarRental, String> statusColumn;
  @FXML
  private Button addButton;
  @FXML
  private Button editButton;
  @FXML
  private Button updateStatusButton;
  @FXML
  private Button reportButton;
  @FXML
  private Button backButton;
  @FXML
  private Button searchButton;
  @FXML
  private Button clearFilterButton;
  @FXML
  private DatePicker startDatePicker;
  @FXML
  private DatePicker endDatePicker;
  @FXML
  private TextField customerNameField;
  @FXML
  private TextField cccdField;
  @FXML
  private ComboBox<String> statusFilterCombo;
  private final CarRentalService carRentalService;
  private final CustomerService customerService;
  private final CarService carService;
  private final Account currentAccount;
  private final ObservableList<CarRental> rentalList;
  public CarRentalManagementController(Account account) {
    this.currentAccount = account;
    this.carRentalService = ServiceLocator.getCarRentalService();
    this.customerService = ServiceLocator.getCustomerService();
    this.carService = ServiceLocator.getCarService();
    this.rentalList = FXCollections.observableArrayList();
  }
  @FXML
  void initialize() {
    setupTable();
    loadRentals();
    setupButtons();
    setupFilters();
  }
  private void setupFilters() {
    statusFilterCombo.getItems().addAll("Tất cả", "Đang thuê", "Đã hoàn thành", "Đã hủy");
    statusFilterCombo.setValue("Tất cả");
  }
  private void setupTable() {
    sttColumn.setCellFactory(column -> new javafx.scene.control.TableCell<CarRental, String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
          setText(null);
        } else {
          int index = getIndex() + 1;
          setText(String.valueOf(index));
        }
      }
    });
    sttColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(""));
    customerNameColumn.setCellValueFactory(cellData -> {
      Customer customer = cellData.getValue().getCustomer();
      return new javafx.beans.property.SimpleStringProperty(
          customer != null ? customer.getCustomerName() : "");
    });
    carNameColumn.setCellValueFactory(cellData -> {
      Car car = cellData.getValue().getCar();
      return new javafx.beans.property.SimpleStringProperty(
          car != null ? car.getCarName() : "");
    });
    pickupDateColumn.setCellValueFactory(cellData -> {
      LocalDate pickupDate = cellData.getValue().getPickupDate();
      String formattedDate = pickupDate != null ? pickupDate.format(DATE_FORMATTER) : "";
      return new javafx.beans.property.SimpleStringProperty(formattedDate);
    });
    pickupDateColumn.setComparator((d1, d2) -> {
      if (d1 == null && d2 == null) return 0;
      if (d1 == null || d1.isEmpty()) return 1;
      if (d2 == null || d2.isEmpty()) return -1;
      try {
        LocalDate date1 = LocalDate.parse(d1, DATE_FORMATTER);
        LocalDate date2 = LocalDate.parse(d2, DATE_FORMATTER);
        return date1.compareTo(date2);
      } catch (Exception e) {
        return d1.compareTo(d2);
      }
    });
    returnDateColumn.setCellValueFactory(cellData -> {
      LocalDate returnDate = cellData.getValue().getReturnDate();
      String formattedDate = returnDate != null ? returnDate.format(DATE_FORMATTER) : "";
      return new javafx.beans.property.SimpleStringProperty(formattedDate);
    });
    returnDateColumn.setComparator((d1, d2) -> {
      if (d1 == null && d2 == null) return 0;
      if (d1 == null || d1.isEmpty()) return 1;
      if (d2 == null || d2.isEmpty()) return -1;
      try {
        LocalDate date1 = LocalDate.parse(d1, DATE_FORMATTER);
        LocalDate date2 = LocalDate.parse(d2, DATE_FORMATTER);
        return date1.compareTo(date2);
      } catch (Exception e) {
        return d1.compareTo(d2);
      }
    });
    rentPriceColumn.setCellValueFactory(cellData -> {
      BigDecimal rentPrice = cellData.getValue().getRentPrice();
      String formattedPrice = rentPrice != null ? PRICE_FORMATTER.format(rentPrice) : "";
      return new javafx.beans.property.SimpleStringProperty(formattedPrice);
    });
    rentPriceColumn.setComparator((p1, p2) -> {
      if (p1 == null && p2 == null) return 0;
      if (p1 == null || p1.isEmpty()) return 1;
      if (p2 == null || p2.isEmpty()) return -1;
      try {
        BigDecimal price1 = new BigDecimal(p1.replace(".", ""));
        BigDecimal price2 = new BigDecimal(p2.replace(".", ""));
        return price1.compareTo(price2);
      } catch (Exception e) {
        return p1.compareTo(p2);
      }
    });
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    rentalTable.setItems(rentalList);
    rentalTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    rentalList.addListener((javafx.collections.ListChangeListener<CarRental>) c -> {
      while (c.next()) {
        if (c.wasUpdated() || c.wasAdded() || c.wasRemoved() || c.wasReplaced()) {
          javafx.application.Platform.runLater(() -> {
            rentalTable.refresh();
          });
        }
      }
    });
  }
  private void setupButtons() {
    editButton.disableProperty().bind(
        rentalTable.getSelectionModel().selectedItemProperty().isNull());
    updateStatusButton.disableProperty().bind(
        rentalTable.getSelectionModel().selectedItemProperty().isNull());
  }
  private void loadRentals() {
    try {
      rentalList.clear();
      rentalList.addAll(carRentalService.getAllRentals());
      rentalList.sort((r1, r2) -> {
        LocalDate d1 = r1.getPickupDate();
        LocalDate d2 = r2.getPickupDate();
        if (d1 == null && d2 == null) return 0;
        if (d1 == null) return 1;
        if (d2 == null) return -1;
        return d2.compareTo(d1); // Giảm dần
      });
      rentalTable.refresh();
    } catch (Exception e) {
      showError("Lỗi", "Không thể tải danh sách giao dịch: " + e.getMessage());
    }
  }
  private void applyFilters(LocalDate startDate, LocalDate endDate, 
                           String customerName, String cccd, String status) {
    try {
      rentalList.clear();
      List<CarRental> filteredRentals;
      if (startDate != null && endDate != null) {
        filteredRentals = carRentalService.getRentalsByDateRange(startDate, endDate);
      } else {
        filteredRentals = carRentalService.getAllRentals();
      }
      if (customerName != null && !customerName.isEmpty()) {
        filteredRentals = filteredRentals.stream()
            .filter(r -> {
              Customer customer = r.getCustomer();
              return customer != null && 
                     customer.getCustomerName().toLowerCase().contains(customerName.toLowerCase());
            })
            .collect(java.util.stream.Collectors.toList());
      }
      if (cccd != null && !cccd.isEmpty()) {
        filteredRentals = filteredRentals.stream()
            .filter(r -> {
              Customer customer = r.getCustomer();
              return customer != null && 
                     customer.getIdentityCard() != null &&
                     customer.getIdentityCard().contains(cccd);
            })
            .collect(java.util.stream.Collectors.toList());
      }
      if (status != null && !"Tất cả".equals(status)) {
        filteredRentals = filteredRentals.stream()
            .filter(r -> status.equals(r.getStatus()))
            .collect(java.util.stream.Collectors.toList());
      }
      rentalList.addAll(filteredRentals);
      rentalList.sort((r1, r2) -> {
        LocalDate d1 = r1.getPickupDate();
        LocalDate d2 = r2.getPickupDate();
        if (d1 == null && d2 == null) return 0;
        if (d1 == null) return 1;
        if (d2 == null) return -1;
        return d2.compareTo(d1); // Giảm dần
      });
    } catch (Exception e) {
      showError("Lỗi", "Không thể tìm kiếm dữ liệu: " + e.getMessage());
    }
  }
  @FXML
  void handleAdd(ActionEvent event) {
    showRentalDialog(null);
  }
  @FXML
  void handleEdit(ActionEvent event) {
    CarRental selected = rentalTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      showRentalDialog(selected);
    }
  }
  @FXML
  void handleUpdateStatus(ActionEvent event) {
    CarRental selected = rentalTable.getSelectionModel().getSelectedItem();
    if (selected != null) {
      showStatusUpdateDialog(selected);
    }
  }
  @FXML
  void handleReport(ActionEvent event) {
    showReportDialog();
  }
  @FXML
  void handleBack(ActionEvent event) {
    SceneNavigator.showDashboard(currentAccount);
  }
  @FXML
  void handleSearch(ActionEvent event) {
    applyFilters(
        startDatePicker.getValue(),
        endDatePicker.getValue(),
        customerNameField.getText().trim(),
        cccdField.getText().trim(),
        statusFilterCombo.getValue()
    );
  }
  @FXML
  void handleClearFilter(ActionEvent event) {
    startDatePicker.setValue(null);
    endDatePicker.setValue(null);
    customerNameField.clear();
    cccdField.clear();
    statusFilterCombo.setValue("Tất cả");
    loadRentals();
  }
  private void showRentalDialog(CarRental rental) {
    Dialog<CarRental> dialog = new Dialog<>();
    dialog.setTitle(rental == null ? "Tạo giao dịch thuê xe mới" : "Chỉnh sửa giao dịch thuê xe");
    dialog.setHeaderText(rental == null ? "Nhập thông tin hợp đồng thuê xe" : "Cập nhật thông tin giao dịch");
    ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    ComboBox<Customer> customerCombo = new ComboBox<>();
    customerCombo.setPromptText("Chọn khách hàng");
    customerCombo.getItems().addAll(customerService.getAllCustomers());
    customerCombo.setCellFactory(param -> new javafx.scene.control.ListCell<Customer>() {
      @Override
      protected void updateItem(Customer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getCustomerName());
        }
      }
    });
    customerCombo.setButtonCell(new javafx.scene.control.ListCell<Customer>() {
      @Override
      protected void updateItem(Customer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getCustomerName());
        }
      }
    });
    ComboBox<Car> carCombo = new ComboBox<>();
    carCombo.setPromptText("Chọn xe");
    if (rental == null) {
      carCombo.getItems().addAll(
          carService.getAllCars().stream()
              .filter(car -> {
                String status = car.getStatus();
                return "Available".equals(status);
              })
              .collect(Collectors.toList())
      );
    } else {
      carCombo.getItems().addAll(carService.getAllCars());
    }
    carCombo.setCellFactory(param -> new javafx.scene.control.ListCell<Car>() {
      @Override
      protected void updateItem(Car item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getCarName());
        }
      }
    });
    carCombo.setButtonCell(new javafx.scene.control.ListCell<Car>() {
      @Override
      protected void updateItem(Car item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item.getCarName());
        }
      }
    });
    Spinner<Integer> pickupDaySpinner = new Spinner<>(1, 31, 1);
    pickupDaySpinner.setEditable(true);
    Spinner<Integer> pickupMonthSpinner = new Spinner<>(1, 12, 1);
    pickupMonthSpinner.setEditable(true);
    Spinner<Integer> pickupYearSpinner = new Spinner<>(2020, 2030, 2024);
    pickupYearSpinner.setEditable(true);
    pickupMonthSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
      try {
        int maxDay = java.time.YearMonth.of(
            pickupYearSpinner.getValue(),
            newVal
        ).lengthOfMonth();
        if (pickupDaySpinner.getValue() > maxDay) {
          pickupDaySpinner.getValueFactory().setValue(maxDay);
        }
        pickupDaySpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxDay, pickupDaySpinner.getValue())
        );
      } catch (Exception e) {
      }
    });
    pickupYearSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
      try {
        int maxDay = java.time.YearMonth.of(
            newVal,
            pickupMonthSpinner.getValue()
        ).lengthOfMonth();
        if (pickupDaySpinner.getValue() > maxDay) {
          pickupDaySpinner.getValueFactory().setValue(maxDay);
        }
        pickupDaySpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxDay, pickupDaySpinner.getValue())
        );
      } catch (Exception e) {
      }
    });
    javafx.scene.layout.HBox pickupDateBox = new javafx.scene.layout.HBox(5);
    pickupDateBox.getChildren().addAll(
        new Label("Ngày:"), pickupDaySpinner,
        new Label("Tháng:"), pickupMonthSpinner,
        new Label("Năm:"), pickupYearSpinner
    );
    Spinner<Integer> returnDaySpinner = new Spinner<>(1, 31, 1);
    returnDaySpinner.setEditable(true);
    Spinner<Integer> returnMonthSpinner = new Spinner<>(1, 12, 1);
    returnMonthSpinner.setEditable(true);
    Spinner<Integer> returnYearSpinner = new Spinner<>(2020, 2030, 2024);
    returnYearSpinner.setEditable(true);
    returnMonthSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
      try {
        int maxDay = java.time.YearMonth.of(
            returnYearSpinner.getValue(),
            newVal
        ).lengthOfMonth();
        if (returnDaySpinner.getValue() > maxDay) {
          returnDaySpinner.getValueFactory().setValue(maxDay);
        }
        returnDaySpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxDay, returnDaySpinner.getValue())
        );
      } catch (Exception e) {
      }
    });
    returnYearSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
      try {
        int maxDay = java.time.YearMonth.of(
            newVal,
            returnMonthSpinner.getValue()
        ).lengthOfMonth();
        if (returnDaySpinner.getValue() > maxDay) {
          returnDaySpinner.getValueFactory().setValue(maxDay);
        }
        returnDaySpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxDay, returnDaySpinner.getValue())
        );
      } catch (Exception e) {
      }
    });
    javafx.scene.layout.HBox returnDateBox = new javafx.scene.layout.HBox(5);
    returnDateBox.getChildren().addAll(
        new Label("Ngày:"), returnDaySpinner,
        new Label("Tháng:"), returnMonthSpinner,
        new Label("Năm:"), returnYearSpinner
    );
    TextField rentPriceField = new TextField();
    rentPriceField.setPromptText("Giá thuê");
    ComboBox<String> statusCombo = new ComboBox<>();
    statusCombo.getItems().addAll("Đang thuê", "Đã hoàn thành", "Đã hủy");
    statusCombo.setValue("Đang thuê");
    if (rental != null) {
      customerCombo.setValue(rental.getCustomer());
      carCombo.setValue(rental.getCar());
      if (rental.getPickupDate() != null) {
        LocalDate pickupDate = rental.getPickupDate();
        pickupDaySpinner.getValueFactory().setValue(pickupDate.getDayOfMonth());
        pickupMonthSpinner.getValueFactory().setValue(pickupDate.getMonthValue());
        pickupYearSpinner.getValueFactory().setValue(pickupDate.getYear());
      }
      if (rental.getReturnDate() != null) {
        LocalDate returnDate = rental.getReturnDate();
        returnDaySpinner.getValueFactory().setValue(returnDate.getDayOfMonth());
        returnMonthSpinner.getValueFactory().setValue(returnDate.getMonthValue());
        returnYearSpinner.getValueFactory().setValue(returnDate.getYear());
      }
      rentPriceField.setText(rental.getRentPrice().toString());
      statusCombo.setValue(rental.getStatus());
      customerCombo.setDisable(true);
      carCombo.setDisable(true);
      pickupDaySpinner.setDisable(true);
      pickupMonthSpinner.setDisable(true);
      pickupYearSpinner.setDisable(true);
    }
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
    grid.add(new Label("Khách hàng:"), 0, 0);
    grid.add(customerCombo, 1, 0);
    grid.add(new Label("Xe:"), 0, 1);
    grid.add(carCombo, 1, 1);
    grid.add(new Label("Ngày nhận:"), 0, 2);
    grid.add(pickupDateBox, 1, 2);
    grid.add(new Label("Ngày trả:"), 0, 3);
    grid.add(returnDateBox, 1, 3);
    grid.add(new Label("Giá thuê:"), 0, 4);
    grid.add(rentPriceField, 1, 4);
    grid.add(new Label("Trạng thái:"), 0, 5);
    grid.add(statusCombo, 1, 5);
    dialog.getDialogPane().setContent(grid);
    javafx.beans.binding.BooleanBinding isInvalidPickupDate = new javafx.beans.binding.BooleanBinding() {
      {
        bind(pickupDaySpinner.valueProperty(), pickupMonthSpinner.valueProperty(), pickupYearSpinner.valueProperty());
      }
      @Override
      protected boolean computeValue() {
        try {
          int day = pickupDaySpinner.getValue();
          int month = pickupMonthSpinner.getValue();
          int year = pickupYearSpinner.getValue();
          LocalDate.of(year, month, day);
          return false; // Valid = false (not invalid)
        } catch (Exception e) {
          return true; // Invalid = true
        }
      }
    };
    javafx.beans.binding.BooleanBinding isInvalidReturnDate = new javafx.beans.binding.BooleanBinding() {
      {
        bind(returnDaySpinner.valueProperty(), returnMonthSpinner.valueProperty(), returnYearSpinner.valueProperty());
      }
      @Override
      protected boolean computeValue() {
        try {
          int day = returnDaySpinner.getValue();
          int month = returnMonthSpinner.getValue();
          int year = returnYearSpinner.getValue();
          LocalDate.of(year, month, day);
          return false; // Valid = false (not invalid)
        } catch (Exception e) {
          return true; // Invalid = true
        }
      }
    };
    Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
    if (rental == null) {
      saveButton.disableProperty().bind(
          customerCombo.valueProperty().isNull()
              .or(carCombo.valueProperty().isNull())
              .or(isInvalidPickupDate)
              .or(isInvalidReturnDate)
              .or(rentPriceField.textProperty().isEmpty()));
    } else {
      saveButton.disableProperty().bind(
          isInvalidReturnDate
              .or(rentPriceField.textProperty().isEmpty()));
    }
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        try {
          LocalDate pickupDate = LocalDate.of(
              pickupYearSpinner.getValue(),
              pickupMonthSpinner.getValue(),
              pickupDaySpinner.getValue()
          );
          LocalDate returnDate = LocalDate.of(
              returnYearSpinner.getValue(),
              returnMonthSpinner.getValue(),
              returnDaySpinner.getValue()
          );
          if (rental == null) {
            BigDecimal rentPrice = new BigDecimal(rentPriceField.getText().trim());
            CarRental newRental = carRentalService.createRental(
                customerCombo.getValue().getId(),
                carCombo.getValue().getId(),
                pickupDate,
                returnDate,
                rentPrice,
                statusCombo.getValue()
            );
            showSuccess("Thành công", "Đã tạo giao dịch thuê xe thành công");
            return newRental;
          } else {
            BigDecimal rentPrice = new BigDecimal(rentPriceField.getText().trim());
            CarRental updatedRental = carRentalService.updateRental(
                rental.getId(),
                returnDate,
                rentPrice,
                statusCombo.getValue()
            );
            showSuccess("Thành công", "Đã cập nhật giao dịch thuê xe thành công");
            return updatedRental;
          }
        } catch (Exception e) {
          showError("Lỗi", e.getMessage());
          return null;
        }
      }
      return null;
    });
    Optional<CarRental> result = dialog.showAndWait();
    if (result.isPresent()) {
      startDatePicker.setValue(null);
      endDatePicker.setValue(null);
      customerNameField.clear();
      cccdField.clear();
      statusFilterCombo.setValue("Tất cả");
      loadRentals();
      rentalTable.refresh();
    }
  }
  private void showStatusUpdateDialog(CarRental rental) {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Cập nhật trạng thái");
    dialog.setHeaderText("Cập nhật trạng thái giao dịch thuê xe");
    ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
    ComboBox<String> statusCombo = new ComboBox<>();
    statusCombo.getItems().addAll("Đang thuê", "Đã hoàn thành", "Đã hủy");
    statusCombo.setValue(rental.getStatus());
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
    grid.add(new Label("Trạng thái mới:"), 0, 0);
    grid.add(statusCombo, 1, 0);
    dialog.getDialogPane().setContent(grid);
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == saveButtonType) {
        try {
          carRentalService.updateRentalStatus(rental.getId(), statusCombo.getValue());
          showSuccess("Thành công", "Đã cập nhật trạng thái thành công");
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
      loadRentals();
    }
  }
  private void showReportDialog() {
    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("Báo cáo thống kê giao dịch thuê xe");
    dialog.setHeaderText("Chọn loại báo cáo và khoảng thời gian");
    ButtonType viewButtonType = new ButtonType("Xem báo cáo", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(viewButtonType, ButtonType.CANCEL);
    ComboBox<String> reportTypeCombo = new ComboBox<>();
    reportTypeCombo.getItems().addAll("Theo khoảng ngày", "Theo tuần", "Theo tháng", "Theo quý", "Theo năm");
    reportTypeCombo.setValue("Theo khoảng ngày");
    DatePicker startDatePicker = new DatePicker();
    startDatePicker.setPromptText("Từ ngày");
    DatePicker endDatePicker = new DatePicker();
    endDatePicker.setPromptText("Đến ngày");
    DatePicker weekDatePicker = new DatePicker();
    weekDatePicker.setPromptText("Chọn ngày trong tuần");
    Spinner<Integer> monthSpinner = new Spinner<>(1, 12, java.time.LocalDate.now().getMonthValue());
    monthSpinner.setEditable(true);
    Spinner<Integer> yearSpinner = new Spinner<>(2020, 2030, java.time.LocalDate.now().getYear());
    yearSpinner.setEditable(true);
    ComboBox<String> quarterCombo = new ComboBox<>();
    quarterCombo.getItems().addAll("Quý 1", "Quý 2", "Quý 3", "Quý 4");
    quarterCombo.setValue("Quý 1");
    Spinner<Integer> quarterYearSpinner = new Spinner<>(2020, 2030, java.time.LocalDate.now().getYear());
    quarterYearSpinner.setEditable(true);
    Spinner<Integer> reportYearSpinner = new Spinner<>(2020, 2030, java.time.LocalDate.now().getYear());
    reportYearSpinner.setEditable(true);
    ComboBox<String> sortOrderCombo = new ComboBox<>();
    sortOrderCombo.getItems().addAll("Giảm dần (mới nhất trước)", "Tăng dần (cũ nhất trước)");
    sortOrderCombo.setValue("Giảm dần (mới nhất trước)");
    javafx.scene.layout.VBox fieldsContainer = new javafx.scene.layout.VBox(10);
    fieldsContainer.getChildren().add(createDateRangeFields(startDatePicker, endDatePicker));
    reportTypeCombo.setOnAction(e -> {
      fieldsContainer.getChildren().clear();
      String selectedType = reportTypeCombo.getValue();
      if ("Theo khoảng ngày".equals(selectedType)) {
        fieldsContainer.getChildren().add(createDateRangeFields(startDatePicker, endDatePicker));
      } else if ("Theo tuần".equals(selectedType)) {
        fieldsContainer.getChildren().add(createWeekFields(weekDatePicker));
      } else if ("Theo tháng".equals(selectedType)) {
        fieldsContainer.getChildren().add(createMonthFields(monthSpinner, yearSpinner));
      } else if ("Theo quý".equals(selectedType)) {
        fieldsContainer.getChildren().add(createQuarterFields(quarterCombo, quarterYearSpinner));
      } else if ("Theo năm".equals(selectedType)) {
        fieldsContainer.getChildren().add(createYearFields(reportYearSpinner));
      }
    });
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
    grid.add(new Label("Loại báo cáo:"), 0, 0);
    grid.add(reportTypeCombo, 1, 0);
    grid.add(new Label("Sắp xếp:"), 0, 1);
    grid.add(sortOrderCombo, 1, 1);
    grid.add(fieldsContainer, 0, 2, 2, 1);
    dialog.getDialogPane().setContent(grid);
    Button viewButton = (Button) dialog.getDialogPane().lookupButton(viewButtonType);
    viewButton.disableProperty().bind(
        javafx.beans.binding.Bindings.createBooleanBinding(() -> {
          String type = reportTypeCombo.getValue();
          if ("Theo khoảng ngày".equals(type)) {
            return startDatePicker.getValue() == null || endDatePicker.getValue() == null;
          } else if ("Theo tuần".equals(type)) {
            return weekDatePicker.getValue() == null;
          }
          return false;
        }, reportTypeCombo.valueProperty(), startDatePicker.valueProperty(), 
           endDatePicker.valueProperty(), weekDatePicker.valueProperty()));
    dialog.setResultConverter(dialogButton -> {
      if (dialogButton == viewButtonType) {
        try {
          LocalDate startDate = null;
          LocalDate endDate = null;
          String selectedType = reportTypeCombo.getValue();
          if ("Theo khoảng ngày".equals(selectedType)) {
            startDate = startDatePicker.getValue();
            endDate = endDatePicker.getValue();
          } else if ("Theo tuần".equals(selectedType)) {
            LocalDate selectedDate = weekDatePicker.getValue();
            if (selectedDate != null) {
              startDate = selectedDate.with(java.time.DayOfWeek.MONDAY);
              endDate = selectedDate.with(java.time.DayOfWeek.SUNDAY);
            }
          } else if ("Theo tháng".equals(selectedType)) {
            int month = monthSpinner.getValue();
            int year = yearSpinner.getValue();
            startDate = LocalDate.of(year, month, 1);
            endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
          } else if ("Theo quý".equals(selectedType)) {
            String quarter = quarterCombo.getValue();
            int year = quarterYearSpinner.getValue();
            int startMonth = 1;
            if ("Quý 1".equals(quarter)) {
              startMonth = 1;
            } else if ("Quý 2".equals(quarter)) {
              startMonth = 4;
            } else if ("Quý 3".equals(quarter)) {
              startMonth = 7;
            } else if ("Quý 4".equals(quarter)) {
              startMonth = 10;
            }
            startDate = LocalDate.of(year, startMonth, 1);
            endDate = startDate.plusMonths(2).withDayOfMonth(
                startDate.plusMonths(2).lengthOfMonth());
          } else if ("Theo năm".equals(selectedType)) {
            int year = reportYearSpinner.getValue();
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
          }
          if (startDate != null && endDate != null) {
            rentalList.clear();
            rentalList.addAll(carRentalService.getRentalReport(startDate, endDate));
            String sortOrder = sortOrderCombo.getValue();
            boolean isDescending = "Giảm dần (mới nhất trước)".equals(sortOrder);
            rentalList.sort((r1, r2) -> {
              LocalDate d1 = r1.getPickupDate();
              LocalDate d2 = r2.getPickupDate();
              if (d1 == null && d2 == null) return 0;
              if (d1 == null) return 1;
              if (d2 == null) return -1;
              int comparison = d1.compareTo(d2);
              return isDescending ? -comparison : comparison; // Giảm dần nếu chọn "Giảm dần", ngược lại tăng dần
            });
            rentalTable.refresh();
            int totalRentals = rentalList.size();
            BigDecimal totalRevenue = rentalList.stream()
                .map(CarRental::getRentPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            String formattedRevenue = PRICE_FORMATTER.format(totalRevenue);
            showSuccess("Báo cáo", 
                String.format("Tổng số giao dịch: %d\nTổng doanh thu: %s VNĐ\nKhoảng thời gian: %s đến %s", 
                    totalRentals, formattedRevenue, 
                    startDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER)));
          }
        } catch (Exception e) {
          showError("Lỗi", e.getMessage());
        }
      }
      return null;
    });
    dialog.showAndWait();
  }
  private javafx.scene.layout.GridPane createDateRangeFields(DatePicker startDate, DatePicker endDate) {
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Từ ngày:"), 0, 0);
    grid.add(startDate, 1, 0);
    grid.add(new Label("Đến ngày:"), 0, 1);
    grid.add(endDate, 1, 1);
    return grid;
  }
  private javafx.scene.layout.GridPane createWeekFields(DatePicker weekDate) {
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Chọn ngày trong tuần:"), 0, 0);
    grid.add(weekDate, 1, 0);
    return grid;
  }
  private javafx.scene.layout.GridPane createMonthFields(Spinner<Integer> month, Spinner<Integer> year) {
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Tháng:"), 0, 0);
    grid.add(month, 1, 0);
    grid.add(new Label("Năm:"), 0, 1);
    grid.add(year, 1, 1);
    return grid;
  }
  private javafx.scene.layout.GridPane createQuarterFields(ComboBox<String> quarter, Spinner<Integer> year) {
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Quý:"), 0, 0);
    grid.add(quarter, 1, 0);
    grid.add(new Label("Năm:"), 0, 1);
    grid.add(year, 1, 1);
    return grid;
  }
  private javafx.scene.layout.GridPane createYearFields(Spinner<Integer> year) {
    javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Năm:"), 0, 0);
    grid.add(year, 1, 0);
    return grid;
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
