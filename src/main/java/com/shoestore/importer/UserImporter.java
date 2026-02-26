package com.shoestore.importer;

import com.shoestore.dao.UserDAO;
import com.shoestore.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserImporter {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Импорт пользователей из Excel файла
     */
    public void importFromExcel(String filePath) {
        System.out.println("\n📥 Импорт пользователей из: " + filePath);

        try {
            // Читаем данные из Excel
            List<List<String>> rows = ExcelReader.readExcel(filePath);
            System.out.println("   Прочитано строк: " + rows.size());

            // Преобразуем в объекты User
            List<User> users = new ArrayList<>();
            for (List<String> row : rows) {
                if (row.size() >= 4) {
                    String role = mapRole(row.get(0));
                    String fullName = row.get(1);
                    String login = row.get(2);
                    String password = row.get(3);

                    User user = new User(fullName, login, password, role);
                    users.add(user);
                    System.out.println("   Подготовлен: " + login + " (" + role + ")");
                }
            }

            // Сохраняем в БД
            System.out.println("   Сохранение в базу данных...");
            userDAO.saveAll(users);

            // Проверка результата
            List<User> savedUsers = userDAO.getAll();
            System.out.println("    Всего пользователей в БД: " + savedUsers.size());

            // Покажем первых 5
            System.out.println("\n Первые 5 пользователей:");
            savedUsers.stream().limit(5).forEach(u ->
                    System.out.println("   - " + u.getLogin() + " (" + u.getRole() + ")")
            );

        } catch (IOException e) {
            System.err.println(" Ошибка чтения файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Преобразование роли из Excel в формат для БД
     */
    private String mapRole(String excelRole) {
        switch (excelRole.trim()) {
            case "Администратор":
                return "ADMIN";
            case "Менеджер":
                return "MANAGER";
            case "Авторизированный клиент":
                return "CLIENT";
            default:
                return "CLIENT";
        }
    }

    public static void main(String[] args) {
        UserImporter importer = new UserImporter();
        // Путь к файлу с данными пользователей
        importer.importFromExcel("data/user_import.xlsx");
    }
}