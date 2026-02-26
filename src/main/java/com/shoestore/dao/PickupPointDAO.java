package com.shoestore.dao;

import com.shoestore.model.PickupPoint;
import com.shoestore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PickupPointDAO {

    /**
     * Сохранить пункт выдачи
     */
    public void save(PickupPoint pickupPoint) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(pickupPoint);
            transaction.commit();
            System.out.println("    Пункт выдачи сохранён: " + pickupPoint.getAddress());
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка сохранения пункта выдачи: " + e.getMessage());
        }
    }

    /**
     * Сохранить список пунктов выдачи
     */
    public void saveAll(List<PickupPoint> pickupPoints) {
        int successCount = 0;
        int errorCount = 0;

        for (PickupPoint point : pickupPoints) {
            try {
                save(point);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                System.err.println("    Не удалось сохранить пункт: " + point.getAddress());
            }
        }

        System.out.println("   📊 Итог: сохранено " + successCount + ", ошибок " + errorCount);
    }

    /**
     * Получить все пункты выдачи
     */
    public List<PickupPoint> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM PickupPoint ORDER BY id", PickupPoint.class).list();
        } catch (Exception e) {
            System.err.println("    Ошибка получения списка пунктов: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Получить количество пунктов выдачи
     */
    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(p) FROM PickupPoint p", Long.class).uniqueResult();
        } catch (Exception e) {
            System.err.println("    Ошибка подсчёта пунктов: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Удалить все пункты выдачи
     */
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM PickupPoint").executeUpdate();
            transaction.commit();
            System.out.println("    Все пункты выдачи удалены");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка удаления пунктов: " + e.getMessage());
        }
    }
}