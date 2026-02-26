package com.shoestore.importer;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Утилита для чтения Excel файлов (.xlsx)
 */
public class ExcelReader {

    /**
     * Читает Excel файл и возвращает список строк (каждая строка - список ячеек)
     */
    public static List<List<String>> readExcel(String filePath) throws IOException {
        List<List<String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Читаем первый лист
            Iterator<Row> rowIterator = sheet.iterator();

            // Пропускаем заголовок (первую строку)
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            // Читаем данные
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<String> rowData = new ArrayList<>();

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = getCellValueAsString(cell);
                    rowData.add(cellValue);
                }

                // Добавляем только непустые строки
                if (!rowData.stream().allMatch(String::isEmpty)) {
                    data.add(rowData);
                }
            }
        }

        return data;
    }

    /**
     * Преобразует ячейку Excel в строку
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double num = cell.getNumericCellValue();
                    // Если число целое, возвращаем без десятичных знаков
                    if (num == Math.floor(num)) {
                        return String.valueOf((int) num);
                    }
                    return String.valueOf(num);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}