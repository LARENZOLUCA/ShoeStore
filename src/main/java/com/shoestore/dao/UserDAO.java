package com.shoestore.dao;

import com.shoestore.model.User;
import com.shoestore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserDAO {

    /**
     * Сохранить пользователя в БД
     */
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Хешируем пароль перед сохранением
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);

            session.persist(user);
            transaction.commit();
            System.out.println("    Пользователь сохранён: " + user.getLogin());

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка сохранения пользователя " + user.getLogin() + ": " + e.getMessage());
        }
    }

    /**
     * Сохранить список пользователей
     */
    public void saveAll(List<User> users) {
        int successCount = 0;
        int errorCount = 0;

        for (User user : users) {
            try {
                save(user);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                System.err.println("    Не удалось сохранить пользователя: " + user.getLogin());
            }
        }

        System.out.println("    Итог: сохранено " + successCount + ", ошибок " + errorCount);
    }

    /**
     * Получить пользователя по логину
     */
    public User findByLogin(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResult();
        } catch (Exception e) {
            System.err.println("    Ошибка поиска пользователя: " + e.getMessage());
            return null;
        }
    }

    /**
     * Проверить пароль пользователя
     */
    public boolean checkPassword(String login, String plainPassword) {
        try {
            User user = findByLogin(login);
            if (user == null) {
                System.out.println("    Пользователь не найден: " + login);
                return false;
            }
            return BCrypt.checkpw(plainPassword, user.getPassword());
        } catch (Exception e) {
            System.err.println("    Ошибка проверки пароля: " + e.getMessage());
            return false;
        }
    }

    /**
     * Получить всех пользователей
     */
    public List<User> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User ORDER BY id", User.class).list();
        } catch (Exception e) {
            System.err.println("    Ошибка получения списка пользователей: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Получить пользователя по ID
     */
    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            System.err.println("    Ошибка поиска пользователя по ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Удалить пользователя по ID
     */
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                System.out.println("    Пользователь удалён: " + user.getLogin());
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка удаления пользователя: " + e.getMessage());
        }
    }

    /**
     * Обновить данные пользователя
     */
    public void update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            System.out.println("    Пользователь обновлён: " + user.getLogin());
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("    Ошибка обновления пользователя: " + e.getMessage());
        }
    }

    /**
     * Получить количество пользователей
     */
    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(u) FROM User u", Long.class).uniqueResult();
        } catch (Exception e) {
            System.err.println("    Ошибка подсчёта пользователей: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Получить пользователей по роли
     */
    public List<User> findByRole(String role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE role = :role", User.class)
                    .setParameter("role", role)
                    .list();
        } catch (Exception e) {
            System.err.println("    Ошибка поиска пользователей по роли: " + e.getMessage());
            return List.of();
        }
    }
}