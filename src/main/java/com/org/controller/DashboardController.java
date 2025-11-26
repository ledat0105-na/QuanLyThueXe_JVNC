package com.org.controller;
import com.org.entity.Account;
import com.org.entity.CarRental;
import com.org.entity.Customer;
import com.org.entity.Review;
import com.org.repository.CustomerRepository;
import com.org.repository.CarRentalRepository;
import com.org.repository.ReviewRepository;
import com.org.repository.impl.HibernateCarRentalRepository;
import com.org.repository.impl.HibernateCustomerRepository;
import com.org.repository.impl.HibernateReviewRepository;
import com.org.service.CarRentalService;
import com.org.service.CustomerService;
import com.org.service.ReviewService;
import com.org.ui.SceneNavigator;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
  @FXML
  private Button manageRentalsButton;
  @FXML
  private VBox adminButtonsPanel;
  @FXML
  private VBox customerTabsContainer;

  @FXML
  private HBox adminLogoutContainer;

  private Button customerLogoutButton;
  private final Account account;
  private CustomerService customerService;
  private CarRentalService carRentalService;
  private ReviewService reviewService;
  private TabPane featureTabPane;
  private ObservableList<CarRental> rentalItems = FXCollections.observableArrayList();
  private ObservableList<Review> reviewItems = FXCollections.observableArrayList();
  private ComboBox<CarRental> reviewRentalSelector;
  private TableView<CarRental> rentalTable;
  private TableView<Review> reviewTable;
  private TextField nameField;
  private TextField mobileField;
  private DatePicker birthdayPicker;
  private TextField identityField;
  private TextField emailField;
  private Customer cachedCustomer;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DecimalFormat PRICE_FORMATTER;
  static {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALIAN);
    symbols.setGroupingSeparator('.');
    PRICE_FORMATTER = new DecimalFormat("#,###", symbols);
  }
  public DashboardController(Account account) {
    this.account = account;
    boolean isAdmin = "Admin".equalsIgnoreCase(account.getRole());
    if (!isAdmin) {
      CustomerRepository customerRepository = new HibernateCustomerRepository();
      CarRentalRepository carRentalRepository = new HibernateCarRentalRepository();
      ReviewRepository reviewRepository = new HibernateReviewRepository();
      this.customerService = new CustomerService(customerRepository);
      this.carRentalService = new CarRentalService(carRentalRepository);
      this.reviewService = new ReviewService(reviewRepository, carRentalRepository);
    }
  }
  @FXML
  void initialize() {
    boolean isAdmin = "Admin".equalsIgnoreCase(account.getRole());
    if (isAdmin) {
      welcomeLabel.setText("Trang quản lý cho thuê xe");
    } else {
      welcomeLabel.setText("Xin chào, " + account.getAccountName());
    }
    roleLabel.setVisible(false);
    roleLabel.setManaged(false);
    logoutButton.setOnAction(event -> SceneNavigator.showLogin());
    if (isAdmin) {
      if (adminButtonsPanel != null) {
        adminButtonsPanel.setVisible(true);
        adminButtonsPanel.setManaged(true);
      }
      if (adminLogoutContainer != null) {
        adminLogoutContainer.setVisible(true);
        adminLogoutContainer.setManaged(true);
      }
      if (customerTabsContainer != null) {
        customerTabsContainer.setVisible(false);
        customerTabsContainer.setManaged(false);
      }
      manageCustomersButton.setOnAction(event -> SceneNavigator.showCustomerManagement(account));
      manageCarsButton.setOnAction(event -> SceneNavigator.showCarManagement(account));
      manageRentalsButton.setOnAction(event -> SceneNavigator.showCarRentalManagement(account));
    } else {
      if (adminButtonsPanel != null) {
        adminButtonsPanel.setVisible(false);
        adminButtonsPanel.setManaged(false);
      }
      if (adminLogoutContainer != null) {
        adminLogoutContainer.setVisible(false);
        adminLogoutContainer.setManaged(false);
      }
      if (customerTabsContainer != null) {
        customerTabsContainer.setVisible(true);
        customerTabsContainer.setManaged(true);
      }
      customerService.findByAccount(account)
          .ifPresentOrElse(customer -> {
                this.cachedCustomer = customer;
                setupFeatureTabs();
                refreshAllData();
              },
              () -> System.out.println("Không tìm thấy khách hàng cho tài khoản hiện tại"));
    }
  }
  public Customer getCurrentCustomerProfile() {
    ensureCustomerLoaded();
    return cachedCustomer;
  }
  public Customer updateProfile(String fullName,
                                String mobile,
                                LocalDate birthday,
                                String identityCard,
                                String email) {
    ensureCustomerLoaded();
    cachedCustomer.setCustomerName(fullName);
    cachedCustomer.setMobile(mobile);
    cachedCustomer.setBirthday(birthday);
    cachedCustomer.setIdentityCard(identityCard);
    cachedCustomer.setEmail(email);
    cachedCustomer = customerService.saveProfile(cachedCustomer);
    return cachedCustomer;
  }
  public List<CarRental> getRentalHistory() {
    ensureCustomerLoaded();
    return carRentalService.findRentals(cachedCustomer);
  }
  public List<Review> getReviews() {
    ensureCustomerLoaded();
    return reviewService.findReviews(cachedCustomer);
  }
  public Review submitReview(Long carId, int star, String comment) {
    ensureCustomerLoaded();
    return reviewService.submitReview(cachedCustomer, carId, star, comment);
  }
  private void ensureCustomerLoaded() {
    if (cachedCustomer == null) {
      throw new IllegalStateException("Chưa có thông tin khách hàng. Vui lòng thử đăng nhập lại.");
    }
  }
  private void setupFeatureTabs() {
    if (featureTabPane != null) {
      return;
    }
    if (adminButtonsPanel != null) {
      adminButtonsPanel.setVisible(false);
      adminButtonsPanel.setManaged(false);
    }
    featureTabPane = new TabPane();
    featureTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    VBox.setVgrow(featureTabPane, Priority.ALWAYS);
    featureTabPane.getTabs().add(createProfileTab());
    featureTabPane.getTabs().add(createHistoryTab());
    featureTabPane.getTabs().add(createReviewTab());
    if (customerTabsContainer != null) {
      customerTabsContainer.getChildren().add(featureTabPane);
      VBox.setVgrow(featureTabPane, Priority.ALWAYS);
      
      customerLogoutButton = new Button("Đăng xuất");
      customerLogoutButton.getStyleClass().add("logout-button");
      customerLogoutButton.setPrefWidth(150);
      customerLogoutButton.setMinWidth(100);
      customerLogoutButton.setOnAction(event -> SceneNavigator.showLogin());
      
      HBox logoutContainer = new HBox();
      logoutContainer.setAlignment(javafx.geometry.Pos.CENTER);
      HBox.setHgrow(logoutContainer, Priority.ALWAYS);
      logoutContainer.getChildren().add(customerLogoutButton);
      
      customerTabsContainer.getChildren().add(logoutContainer);
    }
  }
  private Tab createProfileTab() {
    Tab tab = new Tab("Hồ sơ");
    VBox container = new VBox(10);
    container.setPadding(new Insets(20));
    GridPane form = new GridPane();
    form.setHgap(15);
    form.setVgap(15);
    form.setPadding(new Insets(20));
    nameField = new TextField();
    mobileField = new TextField();
    birthdayPicker = new DatePicker();
    identityField = new TextField();
    emailField = new TextField();
    form.addRow(0, new Label("Họ tên:"), nameField);
    form.addRow(1, new Label("Số điện thoại:"), mobileField);
    form.addRow(2, new Label("Ngày sinh:"), birthdayPicker);
    form.addRow(3, new Label("CMND/CCCD:"), identityField);
    form.addRow(4, new Label("Email:"), emailField);
    Button saveButton = new Button("Lưu thay đổi");
    saveButton.setOnAction(event -> {
      try {
        Customer updated = updateProfile(
            nameField.getText(),
            mobileField.getText(),
            birthdayPicker.getValue(),
            identityField.getText(),
            emailField.getText());
        showInfo("Đã cập nhật hồ sơ cho " + updated.getCustomerName());
      } catch (Exception e) {
        showError("Không thể lưu hồ sơ", e.getMessage());
      }
    });
    form.add(saveButton, 1, 5);
    container.getChildren().add(form);
    tab.setContent(container);
    return tab;
  }
  private Tab createHistoryTab() {
    Tab tab = new Tab("Lịch sử thuê");
    BorderPane pane = new BorderPane();
    pane.setPadding(new Insets(20));
    rentalTable = new TableView<>();
    rentalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    TableColumn<CarRental, String> carColumn = new TableColumn<>("Xe");
    carColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
        Optional.ofNullable(param.getValue().getCar()).map(car -> car.getCarName()).orElse("")));
    TableColumn<CarRental, String> pickupColumn = new TableColumn<>("Ngày nhận");
    pickupColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
        formatDate(param.getValue().getPickupDate())));
    TableColumn<CarRental, String> returnColumn = new TableColumn<>("Ngày trả");
    returnColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
        formatDate(param.getValue().getReturnDate())));
    TableColumn<CarRental, String> statusColumn = new TableColumn<>("Trạng thái");
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    TableColumn<CarRental, String> priceColumn = new TableColumn<>("Giá thuê");
    priceColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
        formatMoney(param.getValue().getRentPrice())));
    rentalTable.getColumns().addAll(carColumn, pickupColumn, returnColumn, statusColumn, priceColumn);
    rentalTable.setItems(rentalItems);
    VBox detailBox = new VBox(8);
    detailBox.setPadding(new Insets(15));
    detailBox.getStyleClass().add("button-panel");
    Label detailTitle = new Label("Chi tiết giao dịch");
    detailTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    Label carLabel = new Label();
    Label pickupLabel = new Label();
    Label returnLabel = new Label();
    Label statusLabel = new Label();
    Label priceLabel = new Label();
    detailBox.getChildren().addAll(detailTitle, carLabel, pickupLabel, returnLabel, statusLabel, priceLabel);
    rentalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
      if (newSel == null) {
        carLabel.setText("");
        pickupLabel.setText("");
        returnLabel.setText("");
        statusLabel.setText("");
        priceLabel.setText("");
        return;
      }
      carLabel.setText("Xe: " + Optional.ofNullable(newSel.getCar()).map(car -> car.getCarName()).orElse(""));
      pickupLabel.setText("Ngày nhận: " + formatDate(newSel.getPickupDate()));
      returnLabel.setText("Ngày trả: " + formatDate(newSel.getReturnDate()));
      statusLabel.setText("Trạng thái: " + newSel.getStatus());
      priceLabel.setText("Giá: " + formatMoney(newSel.getRentPrice()));
    });
    pane.setCenter(rentalTable);
    pane.setBottom(detailBox);
    tab.setContent(pane);
    return tab;
  }
  private Tab createReviewTab() {
    Tab tab = new Tab("Đánh giá");
    VBox container = new VBox(15);
    container.setPadding(new Insets(20));
    reviewTable = new TableView<>();
    reviewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    TableColumn<Review, String> carColumn = new TableColumn<>("Xe");
    carColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(
        Optional.ofNullable(param.getValue().getCar()).map(car -> car.getCarName()).orElse("")));
    TableColumn<Review, Integer> starColumn = new TableColumn<>("Số sao");
    starColumn.setCellValueFactory(new PropertyValueFactory<>("reviewStar"));
    TableColumn<Review, String> commentColumn = new TableColumn<>("Nội dung");
    commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
    reviewTable.getColumns().addAll(carColumn, starColumn, commentColumn);
    reviewTable.setItems(reviewItems);
    reviewRentalSelector = new ComboBox<>();
    reviewRentalSelector.setPromptText("Chọn xe đã thuê");
    reviewRentalSelector.setCellFactory(listView -> new CarRentalListCell());
    reviewRentalSelector.setButtonCell(new CarRentalListCell());
    ComboBox<Integer> starSelector = new ComboBox<>();
    starSelector.getItems().addAll(1, 2, 3, 4, 5);
    starSelector.setPromptText("Số sao (1-5)");
    TextArea commentArea = new TextArea();
    commentArea.setPromptText("Nhập đánh giá");
    commentArea.setPrefRowCount(4);
    Button submitButton = new Button("Gửi đánh giá");
    submitButton.setOnAction(event -> {
      CarRental chosenRental = reviewRentalSelector.getValue();
      Integer star = starSelector.getValue();
      String comment = commentArea.getText();
      if (chosenRental == null) {
        showError("Thiếu thông tin", "Vui lòng chọn một xe đã thuê");
        return;
      }
      if (star == null) {
        showError("Thiếu thông tin", "Vui lòng chọn số sao");
        return;
      }
      try {
        submitReview(chosenRental.getCar().getId(), star, comment);
        showInfo("Đã gửi đánh giá");
        starSelector.getSelectionModel().clearSelection();
        commentArea.clear();
        reviewRentalSelector.getSelectionModel().clearSelection();
        refreshReviewData();
      } catch (Exception e) {
        showError("Không thể gửi đánh giá", e.getMessage());
      }
    });
    VBox form = new VBox(10, reviewRentalSelector, starSelector, commentArea, submitButton);
    form.setPadding(new Insets(10));
    Label reviewSentLabel = new Label("Đánh giá đã gửi");
    reviewSentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    Label newReviewLabel = new Label("Gửi đánh giá mới");
    newReviewLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    container.getChildren().addAll(reviewSentLabel, reviewTable, newReviewLabel, form);
    VBox.setVgrow(reviewTable, Priority.ALWAYS);
    tab.setContent(container);
    return tab;
  }
  private void refreshAllData() {
    refreshProfileForm();
    refreshRentalData();
    refreshReviewData();
  }
  private void refreshProfileForm() {
    if (cachedCustomer == null) {
      return;
    }
    if (nameField != null) {
      nameField.setText(cachedCustomer.getCustomerName());
    }
    if (mobileField != null) {
      mobileField.setText(cachedCustomer.getMobile());
    }
    if (birthdayPicker != null) {
      birthdayPicker.setValue(cachedCustomer.getBirthday());
    }
    if (identityField != null) {
      identityField.setText(cachedCustomer.getIdentityCard());
    }
    if (emailField != null) {
      emailField.setText(cachedCustomer.getEmail());
    }
  }
  private void refreshRentalData() {
    rentalItems.setAll(getRentalHistory());
    if (reviewRentalSelector != null) {
      reviewRentalSelector.setItems(FXCollections.observableArrayList(rentalItems));
    }
  }
  private void refreshReviewData() {
    reviewItems.setAll(getReviews());
  }
  private void showInfo(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
  private void showError(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setHeaderText(title);
    alert.setContentText(message);
    alert.showAndWait();
  }
  private String formatDate(LocalDate date) {
    if (date == null) {
      return "";
    }
    return date.format(DATE_FORMATTER);
  }
  private String formatMoney(BigDecimal amount) {
    if (amount == null) {
      return "";
    }
    return PRICE_FORMATTER.format(amount);
  }
  private static class CarRentalListCell extends javafx.scene.control.ListCell<CarRental> {
    @Override
    protected void updateItem(CarRental item, boolean empty) {
      super.updateItem(item, empty);
      if (empty || item == null || item.getCar() == null) {
        setText(null);
      } else {
        setText(item.getCar().getCarName() + " (" + item.getPickupDate() + ")");
      }
    }
  }
}
