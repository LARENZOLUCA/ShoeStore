package com.shoestore.importer;

import com.shoestore.dao.ProductDAO;
import com.shoestore.model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductImporter {

    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Импорт товаров из Excel файла
     */
    public void importFromExcel(String filePath) {
        System.out.println("\n📥 Импорт товаров из: " + filePath);

        try {
            // Читаем данные из Excel
            List<List<String>> rows = ExcelReader.readExcel(filePath);
            System.out.println("   Прочитано строк: " + rows.size());

            // Пропускаем заголовок (первая строка) если он есть
            List<Product> products = new ArrayList<>();
            int startRow = 0;

            // Проверяем, есть ли заголовок (первая ячейка содержит "Артикул")
            if (!rows.isEmpty() && rows.get(0).get(0).equals("Артикул")) {
                startRow = 1;
                System.out.println("   Пропущен заголовок");
            }

            for (int i = startRow; i < rows.size(); i++) {
                List<String> row = rows.get(i);
                if (row.size() >= 11) {
                    try {
                        Product product = parseProduct(row);
                        products.add(product);
                        System.out.println("   Подготовлен: " + product.getArticle() + " - " + product.getName());
                    } catch (Exception e) {
                        System.err.println("   ⚠️ Ошибка парсинга строки " + (i + 1) + ": " + e.getMessage());
                    }
                }
            }

            // Сохраняем в БД
            System.out.println("   Сохранение в базу данных...");
            productDAO.saveAll(products);

            // Проверка результата
            long count = productDAO.count();
            System.out.println("   ✅ Всего товаров в БД: " + count);

            // Покажем первые 5 товаров
            List<Product> saved = productDAO.getAll();
            System.out.println("\n📋 Первые 5 товаров:");
            saved.stream().limit(5).forEach(p ->
                    System.out.println("   - " + p.getArticle() + " | " + p.getName() + " | " + p.getPrice() + " руб. | Скидка: " + p.getDiscount() + "% | На складе: " + p.getStockQuantity())
            );

        } catch (IOException e) {
            System.err.println("❌ Ошибка чтения файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Преобразование строки Excel в объект Product
     */
    private Product parseProduct(List<String> row) {
        Product product = new Product();

        // Артикул (столбец A)
        product.setArticle(row.get(0).trim());

        // Наименование (столбец B)
        product.setName(row.get(1).trim());

        // Единица измерения (столбец C)
        product.setUnit(row.get(2).trim());

        // Цена (столбец D)
        try {
            String priceStr = row.get(3).trim().replace(",", ".");
            product.setPrice(new BigDecimal(priceStr));
        } catch (Exception e) {
            product.setPrice(BigDecimal.ZERO);
        }

        // Поставщик (столбец E)
        product.setSupplier(row.get(4).trim());

        // Производитель (столбец F)
        product.setManufacturer(row.get(5).trim());

        // Категория (столбец G)
        product.setCategory(row.get(6).trim());

        // Скидка (столбец H)
        try {
            product.setDiscount(Integer.parseInt(row.get(7).trim()));
        } catch (Exception e) {
            product.setDiscount(0);
        }

        // Количество на складе (столбец I)
        try {
            product.setStockQuantity(Integer.parseInt(row.get(8).trim()));
        } catch (Exception e) {
            product.setStockQuantity(0);
        }

        // Описание (столбец J)
        if (row.size() > 9) {
            product.setDescription(row.get(9).trim());
        }

        // Фото (столбец K)
        if (row.size() > 10 && !row.get(10).trim().isEmpty()) {
            product.setPhotoPath(row.get(10).trim());
        }

        return product;
    }

    /**
     * Очистить все товары перед импортом (опционально)
     */
    public void clearAll() {
        System.out.println("\n🧹 Очистка таблицы товаров...");
        List<Product> all = productDAO.getAll();
        for (Product p : all) {
            productDAO.delete(p.getArticle());
        }
        System.out.println("   ✅ Все товары удалены");
    }

    public static void main(String[] args) {
        ProductImporter importer = new ProductImporter();



        importer.importFromExcel("data/Tovar.xlsx");
    }
}