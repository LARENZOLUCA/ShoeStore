package com.shoestore.util;

import com.shoestore.model.User;
import com.shoestore.model.Product;
import com.shoestore.model.Order;
import com.shoestore.model.OrderItem;
import com.shoestore.model.PickupPoint;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            System.out.println("🔍 Создание SessionFactory...");

            // Создаем реестр сервисов из конфигурации
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml") // загружает настройки из XML
                    .build();

            System.out.println(" Конфигурация загружена");

            // Добавляем аннотированные классы
            MetadataSources metadataSources = new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(OrderItem.class)
                    .addAnnotatedClass(PickupPoint.class);

            System.out.println(" Классы добавлены в метаданные:");
            System.out.println("   - User");
            System.out.println("   - Product");
            System.out.println("   - Order");
            System.out.println("   - OrderItem");
            System.out.println("   - PickupPoint");

            // Строим SessionFactory
            SessionFactory factory = metadataSources.buildMetadata().buildSessionFactory();

            System.out.println(" SessionFactory успешно создан");
            return factory;

        } catch (Throwable ex) {
            System.err.println(" Ошибка создания SessionFactory:");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println(" SessionFactory закрыт");
        }
    }
}