package com.shoestore.importer;

import com.shoestore.dao.OrderDAO;
import com.shoestore.model.Order;
import com.shoestore.util.HibernateUtil;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderImporter {

    private final OrderDAO orderDAO = new OrderDAO();

    /**
     * Импорт заказов из Excel файла
     */
    public void importFromExcel(String filePath) {
        System.out.println("\n Импорт заказов из: " + filePath);

        try {
            // Читаем данные из Excel
            List<List<String>> rows = ExcelReader.readExcel(filePath);
            System.out.println("   Прочитано строк: " + rows.size());

            // Пропускаем заголовок (первая строка)
            List<Order> orders = new ArrayList<>();
            int startRow = 0;

            if (!rows.isEmpty() && rows.get(0).get(0).equals("Номер заказа")) {
                startRow = 1;
                System.out.println("   Пропущен заголовок");
            }

            for (int i = startRow; i < rows.size(); i++) {
                List<String> row = rows.get(i);
                if (row.size() >= 8) {
                    try {
                        // Создаем заказ
                        Order order = orderDAO.createOrderFromData(row);
                        if (order != null) {
                            // Добавляем позиции заказа из строки с артикулами
                            String itemsString = row.get(1); // Артикул заказа (столбец B)
                            orderDAO.addOrderItemsFromString(order, itemsString);

                            orders.add(order);
                            System.out.println("   Подготовлен заказ: " + order.getOrderNumber() +
                                    " (" + order.getItems().size() + " позиций)");
                        }
                    } catch (Exception e) {
                        System.err.println("   ️ Ошибка обработки строки " + (i + 1) + ": " + e.getMessage());
                    }
                }
            }

            // Сохраняем все заказы в БД
            System.out.println("   Сохранение в базу данных...");
            for (Order order : orders) {
                orderDAO.save(order);
            }

            // Проверка результата
            long count = orderDAO.count();
            System.out.println("    Всего заказов в БД: " + count);

            // Покажем первые 5 заказов
            System.out.println("\n Первые 5 заказов:");

            // Получаем заказы в новой сессии
            List<Order> saved = orderDAO.getAll();
            for (int j = 0; j < Math.min(5, saved.size()); j++) {
                Order o = saved.get(j);

                // Явно инициализируем коллекцию items в рамках сессии
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Order fullOrder = session.get(Order.class, o.getId());
                    if (fullOrder != null) {
                        int itemsCount = fullOrder.getItems().size(); // Инициализируем коллекцию

                        System.out.println("   - Заказ №" + fullOrder.getOrderNumber() +
                                " | Статус: " + fullOrder.getStatus() +
                                " | Позиций: " + itemsCount +
                                " | Клиент: " + (fullOrder.getUser() != null ?
                                fullOrder.getUser().getFullName() : "Неизвестно"));
                    }
                }
            }

        } catch (IOException e) {
            System.err.println(" Ошибка чтения файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        OrderImporter importer = new OrderImporter();
        importer.importFromExcel("data/Заказ_import.xlsx");
    }
}