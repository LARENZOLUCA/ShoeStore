package com.shoestore.controller;

import com.shoestore.ShoeStoreApp;
import com.shoestore.dao.ProductDAO;
import com.shoestore.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductListController {

    @FXML private Label userInfoLabel;
    @FXML private VBox searchPanel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> supplierFilter;
    @FXML private ComboBox<String> sortCombo;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> photoColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, String> descriptionColumn;
    @FXML private TableColumn<Product, String> manufacturerColumn;
    @FXML private TableColumn<Product, String> supplierColumn;
    @FXML private TableColumn<Product, BigDecimal> priceColumn;
    @FXML private TableColumn<Product, String> unitColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, Integer> discountColumn;
    @FXML private HBox adminPanel;
    @FXML private HBox managerPanel;

    private final ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList;
    private FilteredList<Product> filteredData;

    @FXML
    public void initialize() {
        setupUserInfo();
        setupTableColumns();
        loadProducts();
        setupSearchAndFilter();
        setupRoleBasedUI();
        setupSortCombo();
    }

    private void setupUserInfo() {
        if (UserSession.getInstance().isGuest()) {
            userInfoLabel.setText("Гость");
        } else {
            userInfoLabel.setText(UserSession.getInstance().getCurrentUser().getFullName());
        }
    }

    private void setupSortCombo() {
        sortCombo.getItems().addAll(
                "По умолчанию",
                "По количеству (возрастание)",
                "По количеству (убывание)"
        );
        sortCombo.setValue("По умолчанию");
    }

    private void setupTableColumns() {
        // Настройка колонки с фото
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photoPath"));
        photoColumn.setCellFactory(column -> new TableCell<Product, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String photoPath, boolean empty) {
                super.updateItem(photoPath, empty);
                if (empty || photoPath == null || photoPath.isEmpty()) {
                    setGraphic(null);
                    setText("");
                } else {
                    try {
                        File file = new File("src/main/resources/images/" + photoPath);
                        if (file.exists()) {
                            imageView.setImage(new Image(file.toURI().toString()));
                        } else {
                            // Заглушка
                            File stub = new File("src/main/resources/images/picture.png");
                            if (stub.exists()) {
                                imageView.setImage(new Image(stub.toURI().toString()));
                            }
                        }
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        // Остальные колонки
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));

        // Настройка цвета строк
        productTable.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (product == null || empty) {
                    setStyle("");
                } else {
                    // Подсветка строк
                    if (product.getStockQuantity() == 0) {
                        setStyle("-fx-background-color: #ADD8E6;"); // голубой
                    } else if (product.getDiscount() > 15) {
                        setStyle("-fx-background-color: #2E8B57;"); // зелёный
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Настройка отображения цены со скидкой
        priceColumn.setCellFactory(column -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    if (product.getDiscount() > 0) {
                        BigDecimal discountedPrice = product.getPriceWithDiscount();
                        setText(String.format("%.2f -> %.2f", price, discountedPrice));
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setText(String.format("%.2f", price));
                        setStyle("");
                    }
                }
            }
        });
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAll();
        productList = FXCollections.observableArrayList(products);
        filteredData = new FilteredList<>(productList, p -> true);
        productTable.setItems(filteredData);
    }

    private void setupSearchAndFilter() {
        // Поиск
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return product.getName().toLowerCase().contains(lowerCaseFilter)
                        || product.getDescription().toLowerCase().contains(lowerCaseFilter)
                        || product.getCategory().toLowerCase().contains(lowerCaseFilter)
                        || product.getManufacturer().toLowerCase().contains(lowerCaseFilter)
                        || product.getSupplier().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Фильтр по поставщику
        supplierFilter.getItems().add("Все поставщики");
        if (productList != null && !productList.isEmpty()) {
            supplierFilter.getItems().addAll(
                    productList.stream()
                            .map(Product::getSupplier)
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList())
            );
        }
        supplierFilter.setValue("Все поставщики");

        supplierFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || "Все поставщики".equals(newVal)) {
                filteredData.setPredicate(product -> true);
            } else {
                filteredData.setPredicate(product -> newVal.equals(product.getSupplier()));
            }
        });

        // Сортировка
        SortedList<Product> sortedData = new SortedList<>(filteredData);

        sortCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("По количеству (возрастание)".equals(newVal)) {
                sortedData.setComparator(Comparator.comparing(Product::getStockQuantity));
            } else if ("По количеству (убывание)".equals(newVal)) {
                sortedData.setComparator(Comparator.comparing(Product::getStockQuantity).reversed());
            } else {
                sortedData.setComparator(null);
            }
        });

        productTable.setItems(sortedData);
    }

    private void setupRoleBasedUI() {
        if (UserSession.getInstance().isAdmin()) {
            adminPanel.setVisible(true);
            adminPanel.setManaged(true);
            searchPanel.setVisible(true);
            searchPanel.setManaged(true);
        } else if (UserSession.getInstance().isManager()) {
            managerPanel.setVisible(true);
            managerPanel.setManaged(true);
            searchPanel.setVisible(true);
            searchPanel.setManaged(true);
        } else {
            searchPanel.setVisible(false);
            searchPanel.setManaged(false);
        }
    }

    @FXML
    private void handleLogout() {
        try {
            UserSession.getInstance().setCurrentUser(null);
            ShoeStoreApp.showLoginScreen();
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось выйти из системы", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddProduct() {
        if (UserSession.getInstance().isAdmin()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/product_form.fxml"));
                Parent root = loader.load();

                ProductFormController controller = loader.getController();
                controller.setProduct(null); // null = новый товар

                Stage stage = (Stage) productTable.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Добавление товара");
            } catch (Exception e) {
                showAlert("Ошибка", "Не удалось открыть форму: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleEditProduct() {
        if (UserSession.getInstance().isAdmin()) {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/product_form.fxml"));
                    Parent root = loader.load();

                    ProductFormController controller = loader.getController();
                    controller.setProduct(selected);

                    Stage stage = (Stage) productTable.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Редактирование товара");
                } catch (Exception e) {
                    showAlert("Ошибка", "Не удалось открыть форму: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            } else {
                showAlert("Предупреждение", "Выберите товар для редактирования", Alert.AlertType.WARNING);
            }
        }
    }

    @FXML
    private void handleDeleteProduct() {
        if (UserSession.getInstance().isAdmin()) {
            Product selected = productTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // TODO: проверить, есть ли товар в заказах
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Подтверждение");
                confirm.setHeaderText("Удаление товара");
                confirm.setContentText("Вы уверены, что хотите удалить товар " + selected.getName() + "?");

                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        productDAO.delete(selected.getArticle());
                        loadProducts();
                        showAlert("Успех", "Товар удалён", Alert.AlertType.INFORMATION);
                    }
                });
            } else {
                showAlert("Предупреждение", "Выберите товар для удаления", Alert.AlertType.WARNING);
            }
        }
    }

    @FXML
    private void handleViewOrders() {
        // TODO: открыть окно с заказами
        showAlert("Информация", "Просмотр заказов будет позже", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}