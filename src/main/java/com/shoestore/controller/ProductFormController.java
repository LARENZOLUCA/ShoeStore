package com.shoestore.controller;

import com.shoestore.ShoeStoreApp;
import com.shoestore.dao.ProductDAO;
import com.shoestore.model.Product;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductFormController {

    @FXML private Label formTitle;
    @FXML private TextField articleField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> manufacturerCombo;
    @FXML private ComboBox<String> supplierCombo;
    @FXML private ComboBox<String> unitCombo;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField discountField;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView imageView;
    @FXML private Label errorLabel;

    private Product currentProduct;
    private final ProductDAO productDAO = new ProductDAO();
    private String selectedImagePath;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        loadComboBoxData();
        setupValidation();

        // Инициализация единиц измерения
        unitCombo.getItems().addAll("шт.", "пара", "компл.");
        unitCombo.setValue("шт.");
    }

    private void loadComboBoxData() {
        // Загружаем все товары для получения уникальных значений
        List<Product> allProducts = productDAO.getAll();

        // Категории
        categoryCombo.getItems().clear();
        categoryCombo.getItems().addAll(
                allProducts.stream()
                        .map(Product::getCategory)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())
        );

        // Производители
        manufacturerCombo.getItems().clear();
        manufacturerCombo.getItems().addAll(
                allProducts.stream()
                        .map(Product::getManufacturer)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())
        );

        // Поставщики
        supplierCombo.getItems().clear();
        supplierCombo.getItems().addAll(
                allProducts.stream()
                        .map(Product::getSupplier)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())
        );
    }

    private void setupValidation() {
        // Только числа для цен
        priceField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                priceField.setText(old);
            }
        });

        // Только целые числа для количества и скидки
        stockField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                stockField.setText(old);
            }
        });

        discountField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                discountField.setText(old);
            }
        });
    }

    public void setProduct(Product product) {
        this.currentProduct = product;
        this.isEditMode = (product != null);

        if (isEditMode) {
            formTitle.setText("Редактирование товара");
            loadProductData();
        } else {
            formTitle.setText("Добавление товара");
            articleField.setDisable(false);
        }
    }

    private void loadProductData() {
        articleField.setText(currentProduct.getArticle());
        articleField.setDisable(true); // Артикул нельзя менять при редактировании

        nameField.setText(currentProduct.getName());
        categoryCombo.setValue(currentProduct.getCategory());
        manufacturerCombo.setValue(currentProduct.getManufacturer());
        supplierCombo.setValue(currentProduct.getSupplier());
        unitCombo.setValue(currentProduct.getUnit());
        priceField.setText(currentProduct.getPrice().toString());
        stockField.setText(currentProduct.getStockQuantity().toString());
        discountField.setText(currentProduct.getDiscount().toString());
        descriptionArea.setText(currentProduct.getDescription());

        if (currentProduct.getPhotoPath() != null && !currentProduct.getPhotoPath().isEmpty()) {
            loadImage(currentProduct.getPhotoPath());
        }
    }

    private void loadImage(String filename) {
        try {
            File file = new File("src/main/resources/images/" + filename);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                selectedImagePath = filename;
            }
        } catch (Exception e) {
            showError("Не удалось загрузить изображение");
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Проверяем и изменяем размер изображения
                BufferedImage originalImage = ImageIO.read(selectedFile);
                BufferedImage resizedImage = resizeImage(originalImage, 300, 200);

                // Сохраняем в папку images
                String newFilename = System.currentTimeMillis() + "_" + selectedFile.getName();
                File outputFile = new File("src/main/resources/images/" + newFilename);

                // Создаем папку если её нет
                outputFile.getParentFile().mkdirs();

                ImageIO.write(resizedImage, "png", outputFile);

                // Загружаем в ImageView
                Image image = new Image(outputFile.toURI().toString());
                imageView.setImage(image);
                selectedImagePath = newFilename;

            } catch (IOException e) {
                showError("Ошибка при обработке изображения");
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    @FXML
    private void handleDeleteImage() {
        if (selectedImagePath != null) {
            File file = new File("src/main/resources/images/" + selectedImagePath);
            if (file.exists()) {
                file.delete();
            }
            imageView.setImage(null);
            selectedImagePath = null;
        }
    }

    @FXML
    private void handleSave() {
        if (!validateFields()) {
            return;
        }

        try {
            if (isEditMode) {
                updateProduct();
            } else {
                createProduct();
            }

            // Возвращаемся к списку товаров
            ShoeStoreApp.showProductListScreen();

        } catch (Exception e) {
            showError("Ошибка сохранения: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите наименование товара");
            return false;
        }
        if (categoryCombo.getValue() == null || categoryCombo.getValue().trim().isEmpty()) {
            showError("Выберите категорию");
            return false;
        }
        if (manufacturerCombo.getValue() == null || manufacturerCombo.getValue().trim().isEmpty()) {
            showError("Выберите производителя");
            return false;
        }
        if (supplierCombo.getValue() == null || supplierCombo.getValue().trim().isEmpty()) {
            showError("Выберите поставщика");
            return false;
        }
        if (unitCombo.getValue() == null || unitCombo.getValue().trim().isEmpty()) {
            showError("Выберите единицу измерения");
            return false;
        }
        if (priceField.getText().trim().isEmpty()) {
            showError("Введите цену");
            return false;
        }
        if (stockField.getText().trim().isEmpty()) {
            showError("Введите количество на складе");
            return false;
        }

        // Проверка отрицательных значений
        if (new BigDecimal(priceField.getText()).compareTo(BigDecimal.ZERO) < 0) {
            showError("Цена не может быть отрицательной");
            return false;
        }
        if (Integer.parseInt(stockField.getText()) < 0) {
            showError("Количество не может быть отрицательным");
            return false;
        }
        if (!discountField.getText().trim().isEmpty()) {
            int discount = Integer.parseInt(discountField.getText());
            if (discount < 0 || discount > 100) {
                showError("Скидка должна быть от 0 до 100");
                return false;
            }
        }

        return true;
    }

    private void createProduct() {
        Product product = new Product();
        product.setArticle(generateArticle());
        product.setName(nameField.getText().trim());
        product.setCategory(categoryCombo.getValue().trim());
        product.setManufacturer(manufacturerCombo.getValue().trim());
        product.setSupplier(supplierCombo.getValue().trim());
        product.setUnit(unitCombo.getValue().trim());
        product.setPrice(new BigDecimal(priceField.getText().trim()));
        product.setStockQuantity(Integer.parseInt(stockField.getText().trim()));

        String discountText = discountField.getText().trim();
        product.setDiscount(discountText.isEmpty() ? 0 : Integer.parseInt(discountText));

        product.setDescription(descriptionArea.getText().trim());
        product.setPhotoPath(selectedImagePath);

        productDAO.save(product);
    }

    private void updateProduct() {
        currentProduct.setName(nameField.getText().trim());
        currentProduct.setCategory(categoryCombo.getValue().trim());
        currentProduct.setManufacturer(manufacturerCombo.getValue().trim());
        currentProduct.setSupplier(supplierCombo.getValue().trim());
        currentProduct.setUnit(unitCombo.getValue().trim());
        currentProduct.setPrice(new BigDecimal(priceField.getText().trim()));
        currentProduct.setStockQuantity(Integer.parseInt(stockField.getText().trim()));

        String discountText = discountField.getText().trim();
        currentProduct.setDiscount(discountText.isEmpty() ? 0 : Integer.parseInt(discountText));

        currentProduct.setDescription(descriptionArea.getText().trim());
        currentProduct.setPhotoPath(selectedImagePath);

        productDAO.update(currentProduct);
    }

    private String generateArticle() {
        // Простой генератор артикула
        return "ART" + System.currentTimeMillis();
    }

    @FXML
    private void handleCancel() {
        try {
            ShoeStoreApp.showProductListScreen();
        } catch (Exception e) {
            showError("Ошибка при возврате");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }
}