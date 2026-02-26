package com.shoestore.importer;

import java.io.IOException;
import java.util.List;

public class TestImport {
    public static void main(String[] args) {
        String userFile = "C:\\Users\\AlexGG\\IdeaProjects\\ShoeStore\\data\\user_import.xlsx";

        try {
            System.out.println("Чтение файла: " + userFile);
            List<List<String>> data = ExcelReader.readExcel(userFile);

            System.out.println("Найдено строк: " + data.size());
            for (int i = 0; i < Math.min(3, data.size()); i++) {
                System.out.println("Строка " + (i+1) + ": " + data.get(i));
            }

        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            e.printStackTrace();
        }
    }
}