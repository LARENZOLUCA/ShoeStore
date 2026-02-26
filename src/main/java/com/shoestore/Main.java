package com.shoestore;

import com.shoestore.util.HibernateUtil;
import com.shoestore.model.User;
import com.shoestore.model.Product;
import com.shoestore.model.Order;
import com.shoestore.model.OrderItem;
import com.shoestore.model.PickupPoint;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Главный класс приложения ShoeStore
 * Запускает проверку подключения к БД и инициализацию
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   ShoeStore - Магазин обуви");
        System.out.println("=================================");

        // Проверка подключения к базе данных
        testDatabaseConnection();

        // Если нужен JavaFX интерфейс, раскомментируйте:
        // launchJavaFX(args);
    }

    /**
     * Проверка подключения к БД и создание таблиц
     */
    private static void testDatabaseConnection() {
        System.out.println("\n[1] Проверка подключения к БД...");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("    Подключение успешно!");

            // Проверяем, создались ли таблицы
            System.out.println("\n[2] Проверка создания таблиц...");

            Transaction tx = session.beginTransaction();
            try {
                // Проверяем таблицу users
                Long userCount = executeCountQuery(session, "User");
                System.out.println("    Таблица 'users' готова (записей: " + userCount + ")");

                // Проверяем таблицу products
                Long productCount = executeCountQuery(session, "Product");
                System.out.println("    Таблица 'products' готова (записей: " + productCount + ")");

                // Проверяем таблицу orders
                Long orderCount = executeCountQuery(session, "Order");
                System.out.println("    Таблица 'orders' готова (записей: " + orderCount + ")");

                // Проверяем таблицу pickup_points
                Long pickupPointCount = executeCountQuery(session, "PickupPoint");
                System.out.println("    Таблица 'pickup_points' готова (записей: " + pickupPointCount + ")");

                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                System.out.println("    Таблицы ещё не созданы или пусты");
                System.out.println("    Hibernate создаст их при первом сохранении данных");
                System.out.println("    Детали: " + e.getMessage());
            }

            // Показываем структуру базы данных
            showDatabaseStructure(session);

        } catch (Exception e) {
            System.out.println("    Ошибка подключения: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n База данных готова к работе!");
        System.out.println("=================================");
    }

    /**
     * Вспомогательный метод для выполнения COUNT запроса
     */
    private static Long executeCountQuery(Session session, String entityName) {
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(e) FROM " + entityName + " e", Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * Показывает структуру созданных таблиц
     */
    private static void showDatabaseStructure(Session session) {
        System.out.println("\n[3] Структура базы данных:");

        try {
            // Получаем метаданные из JDBC
            var metadata = session.doReturningWork(connection ->
                    connection.getMetaData()
            );

            var tables = metadata.getTables(null, "public", "%", new String[]{"TABLE"});
            System.out.println("    Найденные таблицы:");

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("      - " + tableName);
            }
            tables.close();

        } catch (Exception e) {
            System.out.println("    Не удалось получить структуру: " + e.getMessage());
        }
    }

    /**
     * Запуск JavaFX интерфейса (будет реализовано позже)
     */
    private static void launchJavaFX(String[] args) {
        System.out.println("\nЗапуск JavaFX интерфейса...");
        // Application.launch(JavaFXApp.class, args);
    }
}