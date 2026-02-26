package com.shoestore.dao;

import com.shoestore.model.Order;
import com.shoestore.model.OrderItem;
import com.shoestore.model.User;
import com.shoestore.model.Product;
import com.shoestore.model.PickupPoint;
import com.shoestore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderDAO {

    private final UserDAO userDAO = new UserDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final PickupPointDAO pickupPointDAO = new PickupPointDAO();

    /**
     * Сохранить заказ вместе с его позициями
     */
    public void save(Order order) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Сначала сохраняем заказ
            session.persist(order);

            // Сохраняем все позиции заказа
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                session.persist(item);
            }

            transaction.commit();
            System.out.println("   ✅ Заказ сохранён: " + order.getOrderNumber());
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("   ❌ Ошибка сохранения заказа " + order.getOrderNumber() + ": " + e.getMessage());
        }
    }

    /**
     * Создать заказ из данных Excel
     */
    public Order createOrderFromData(List<String> row) {
        try {
            Order order = new Order();

            // Номер заказа (столбец A)
            order.setOrderNumber(row.get(0).trim());

            // Дата заказа (столбец C)
            String orderDateStr = row.get(2).trim();
            order.setOrderDate(parseDate(orderDateStr));

            // Дата доставки (столбец D)
            String deliveryDateStr = row.get(3).trim();
            if (!deliveryDateStr.isEmpty()) {
                order.setDeliveryDate(parseDate(deliveryDateStr));
            }

            // Адрес пункта выдачи (столбец E) - ищем пункт выдачи
            String address = row.get(4).trim();
            PickupPoint pickupPoint = findPickupPointByAddress(address);
            if (pickupPoint != null) {
                order.setPickupPoint(pickupPoint);
            } else {
                System.err.println("   ⚠️ Пункт выдачи не найден: " + address);
                return null;
            }

            // ФИО пользователя (столбец F) - ищем пользователя в БД
            String userFullName = row.get(5).trim();
            User user = findUserByFullName(userFullName);
            if (user != null) {
                order.setUser(user);
            } else {
                System.err.println("   ⚠️ Пользователь не найден: " + userFullName);
                return null;
            }

            // Код для получения (столбец G)
            order.setPickupCode(row.get(6).trim());

            // Статус заказа (столбец H)
            String status = mapStatus(row.get(7).trim());
            order.setStatus(status);

            return order;

        } catch (Exception e) {
            System.err.println("   ❌ Ошибка создания заказа: " + e.getMessage());
            return null;
        }
    }

    /**
     * Создать позиции заказа из строки с артикулами
     */
    public void addOrderItemsFromString(Order order, String itemsString) {
        try {
            // Пример: "А112Т4, 2, F635R4, 2"
            String[] parts = itemsString.split(",");

            for (int i = 0; i < parts.length; i += 2) {
                if (i + 1 < parts.length) {
                    String article = parts[i].trim();
                    int quantity = Integer.parseInt(parts[i + 1].trim());

                    Product product = productDAO.findByArticle(article);
                    if (product != null) {
                        OrderItem item = new OrderItem();
                        item.setOrder(order);
                        item.setProduct(product);
                        item.setQuantity(quantity);
                        item.setPriceAtOrder(product.getPrice()); // Цена на момент заказа

                        order.getItems().add(item);
                    } else {
                        System.err.println("   ⚠️ Товар не найден: " + article);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("   ❌ Ошибка парсинга позиций заказа: " + e.getMessage());
        }
    }

    /**
     * Поиск пользователя по ФИО
     */
    private User findUserByFullName(String fullName) {
        List<User> users = userDAO.getAll();
        return users.stream()
                .filter(u -> u.getFullName().equalsIgnoreCase(fullName.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Поиск пункта выдачи по адресу
     */
    private PickupPoint findPickupPointByAddress(String address) {
        List<PickupPoint> points = pickupPointDAO.getAll();
        return points.stream()
                .filter(p -> p.getAddress().equalsIgnoreCase(address.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Парсинг даты из разных форматов
     */
    private LocalDateTime parseDate(String dateStr) {
        try {
            // Пробуем разные форматы
            if (dateStr.contains(".") && dateStr.length() == 10) {
                // Формат: 30.02.2025
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                return LocalDateTime.parse(dateStr + " 00:00:00", formatter);
            } else if (dateStr.contains("-")) {
                // Формат: 2025-02-27 00:00:00
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(dateStr, formatter);
            }
        } catch (Exception e) {
            System.err.println("   ⚠️ Ошибка парсинга даты: " + dateStr);
        }
        return LocalDateTime.now();
    }

    /**
     * Преобразование статуса
     */
    private String mapStatus(String status) {
        switch (status.trim().toLowerCase()) {
            case "новый":
                return "NEW";
            case "завершен":
                return "COMPLETED";
            case "отменен":
                return "CANCELLED";
            default:
                return "NEW";
        }
    }

    /**
     * Получить все заказы
     */
    public List<Order> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order ORDER BY id", Order.class).list();
        } catch (Exception e) {
            System.err.println("   ❌ Ошибка получения списка заказов: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Получить количество заказов
     */
    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(o) FROM Order o", Long.class).uniqueResult();
        } catch (Exception e) {
            System.err.println("   ❌ Ошибка подсчёта заказов: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Получить заказы по пункту выдачи
     */
    public List<Order> findByPickupPoint(PickupPoint pickupPoint) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order WHERE pickupPoint = :pickupPoint", Order.class)
                    .setParameter("pickupPoint", pickupPoint)
                    .list();
        } catch (Exception e) {
            System.err.println("    Ошибка поиска заказов по пункту выдачи: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Получить заказы по пользователю
     */
    public List<Order> findByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order WHERE user = :user", Order.class)
                    .setParameter("user", user)
                    .list();
        } catch (Exception e) {
            System.err.println("    Ошибка поиска заказов по пользователю: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Получить заказы по статусу
     */
    public List<Order> findByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Order WHERE status = :status", Order.class)
                    .setParameter("status", status)
                    .list();
        } catch (Exception e) {
            System.err.println("    Ошибка поиска заказов по статусу: " + e.getMessage());
            return List.of();
        }
    }
}