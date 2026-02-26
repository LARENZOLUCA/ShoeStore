package com.shoestore.importer;

import com.shoestore.dao.PickupPointDAO;
import com.shoestore.model.PickupPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PickupPointImporter {

    private final PickupPointDAO pickupPointDAO = new PickupPointDAO();

    /**
     * Импорт пунктов выдачи из Excel файла
     */
    public void importFromExcel(String filePath) {
        System.out.println("\n Импорт пунктов выдачи из: " + filePath);

        try {
            // Читаем данные из Excel
            List<List<String>> rows = ExcelReader.readExcel(filePath);
            System.out.println("   Прочитано строк: " + rows.size());

            // Преобразуем в объекты PickupPoint
            List<PickupPoint> pickupPoints = new ArrayList<>();
            for (List<String> row : rows) {
                if (!row.isEmpty() && !row.get(0).isEmpty()) {
                    String address = row.get(0).trim();
                    PickupPoint point = new PickupPoint(address);
                    pickupPoints.add(point);
                    System.out.println("   Подготовлен: " + address);
                }
            }

            // Сохраняем в БД
            System.out.println("   Сохранение в базу данных...");
            pickupPointDAO.saveAll(pickupPoints);

            // Проверка результата
            long count = pickupPointDAO.count();
            System.out.println("    Всего пунктов выдачи в БД: " + count);

            // Покажем первые 5
            List<PickupPoint> saved = pickupPointDAO.getAll();
            System.out.println("\n Первые 5 пунктов выдачи:");
            saved.stream().limit(5).forEach(p ->
                    System.out.println("   - " + p.getAddress())
            );

        } catch (IOException e) {
            System.err.println(" Ошибка чтения файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PickupPointImporter importer = new PickupPointImporter();
        importer.importFromExcel("data/Пункты выдачи_import.xlsx");
    }
}