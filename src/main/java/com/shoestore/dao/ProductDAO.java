package com.shoestore.dao;

import com.shoestore.model.Product;
import com.shoestore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class ProductDAO {

    /**
     * Сохранить товар
     */
    public void save(Product product) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(product);
            transaction.commit();
            System.out.println("    Товар сохранён: " + product.getName() + " (" + product.getArticle() + ")");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка сохранения товара " + product.getArticle() + ": " + e.getMessage());
        }
    }

    /**
     * Сохранить список товаров
     */
    public void saveAll(List<Product> products) {
        int successCount = 0;
        int errorCount = 0;

        for (Product product : products) {
            try {
                save(product);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                System.err.println("    Не удалось сохранить товар: " + product.getArticle());
            }
        }

        System.out.println("    Итог: сохранено " + successCount + ", ошибок " + errorCount);
    }

    /**
     * Получить товар по артикулу
     */
    public Product findByArticle(String article) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Product.class, article);
        } catch (Exception e) {
            System.err.println("    Ошибка поиска товара: " + e.getMessage());
            return null;
        }
    }

    /**
     * Получить все товары
     */
    public List<Product> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Product ORDER BY article", Product.class).list();
        } catch (Exception e) {
            System.err.println("    Ошибка получения списка товаров: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Получить количество товаров
     */
    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(p) FROM Product p", Long.class).uniqueResult();
        } catch (Exception e) {
            System.err.println("    Ошибка подсчёта товаров: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Обновить товар
     */
    public void update(Product product) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(product);
            transaction.commit();
            System.out.println("    Товар обновлён: " + product.getArticle());
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка обновления товара: " + e.getMessage());
        }
    }

    /**
     * Удалить товар
     */
    public void delete(String article) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, article);
            if (product != null) {
                session.remove(product);
                transaction.commit();
                System.out.println("    Товар удалён: " + article);
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка удаления товара: " + e.getMessage());
        }
    }

    /**
     * Получить товары по категории
     */
    public List<Product> findByCategory(String category) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Product WHERE category = :category", Product.class)
                    .setParameter("category", category)
                    .list();
        } catch (Exception e) {
            System.err.println("    Ошибка поиска по категории: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Получить товары со скидкой больше указанной
     */
    public List<Product> findByDiscountGreaterThan(int discount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Product WHERE discount > :discount", Product.class)
                    .setParameter("discount", discount)
                    .list();
        } catch (Exception e) {
            System.err.println("    Ошибка поиска по скидке: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Обновить количество на складе
     */
    public void updateStock(String article, int newQuantity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Product product = session.get(Product.class, article);
            if (product != null) {
                product.setStockQuantity(newQuantity);
                session.merge(product);
                transaction.commit();
                System.out.println("    Количество обновлено для " + article + ": " + newQuantity);
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка обновления количества: " + e.getMessage());
        }
    }
}