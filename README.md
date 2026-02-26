# ShoeStore - Информационная система магазина обуви

![Java](https://img.shields.io/badge/Java-24-007396?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-007396?style=for-the-badge&logo=java)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.4-336791?style=for-the-badge&logo=postgresql)
![Hibernate](https://img.shields.io/badge/Hibernate-6.4.4-59666C?style=for-the-badge&logo=hibernate)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven)

##  Описание проекта

Информационная система для магазина обуви "ООО Обувь". 
**Основные возможности:**

- Авторизация пользователей с 4 ролями (админ, менеджер, клиент, гость)
- Просмотр товаров с визуальной подсветкой (скидки, наличие)
- Поиск, фильтрация и сортировка товаров
- CRUD операции с товарами для администратора
- Загрузка и изменение изображений товаров (ресайз 300x200)


##  Технологический стек

| Компонент | Технология |
| :-- | :-- |
| Язык программирования | Java 24 |
| GUI | JavaFX 21 |
| ORM | Hibernate 6.4.4 |
| База данных | PostgreSQL 42.7.4 |
| Сборщик | Maven |
| Excel | Apache POI 5.2.5 |
| Хеширование | jBCrypt 0.4 |

##  ER-диаграмма

Файл `ER-diagram.pdf` находится в корне проекта.

##  Роли пользователей
| Роль | Права доступа |
| :-- | :-- |
| **Гость** | Просмотр товаров |
| **Клиент** | Просмотр товаров |
| **Менеджер** | Поиск, фильтрация, сортировка, просмотр заказов (в разработке) |
| **Администратор** | Полный CRUD товаров + все права менеджера |

##  Тестовые учётные записи

| Роль | Логин | Пароль |
| :-- | :-- | :-- |
| **Администратор** | `94d5ous@gmail.com` | `uzWC67` |
| **Администратор** | `uth4iz@mail.com` | `2L6KZG` |
| **Менеджер** | `1diph5e@tutanota.com` | `8ntwUp` |
| **Клиент** | `5d4zbu@tutanota.com` | `rwVDh9` |

Полный список пользователей доступен в файле `data/user_import.xlsx`.

##  Установка и запуск

### Предварительные требования

- JDK 24+
- PostgreSQL 14+
- Maven 3.9+


### Шаг 1: Клонирование репозитория

```bash
git clone https://github.com/ваш-логин/ShoeStore.git
cd ShoeStore
```


### Шаг 2: Настройка базы данных

```bash
# Создайте базу данных
psql -U postgres -c "CREATE DATABASE shoestore;"

# Настройте пароль в src/main/resources/hibernate.cfg.xml
# Выполните скрипт создания таблиц
psql -U postgres -d shoestore -f database_schema.sql
```


### Шаг 3: Импорт тестовых данных (опционально)

```bash
mvn exec:java -Dexec.mainClass="com.shoestore.importer.UserImporter"
mvn exec:java -Dexec.mainClass="com.shoestore.importer.ProductImporter"
```


### Шаг 4: Запуск приложения

```bash
# Через Maven
mvn clean javafx:run

# Или через JAR (укажите пути к JavaFX)
java --module-path "путь_к_javaFX" --add-modules javafx.controls,javafx.fxml -jar target/shoe-store-1.0.0.jar
```


##  Особенности интерфейса

- **Шрифт:** Times New Roman на всех формах
- **Цветовая схема:**
    - Основной фон: \#FFFFFF (белый)
    - Дополнительный фон: \#7FFF00 (кнопки)
    - Акцентирование: \#00FA9A (кнопка сохранения)
    - Скидка >15%: \#2E8B57 (фон строки)
    - Нет в наличии: \#ADD8E6 (фон строки)
    - Цена со скидкой: красный, перечёркнутый


##  Структура проекта

```
ShoeStore/
├── src/
│   ├── main/
│   │   ├── java/com/shoestore/
│   │   │   ├── controller/     # JavaFX контроллеры
│   │   │   ├── dao/            # Доступ к данным
│   │   │   ├── importer/       # Импорт из Excel
│   │   │   ├── model/          # JPA сущности
│   │   │   ├── util/           # Утилиты
│   │   │   └── ShoeStoreApp.java
│   │   └── resources/
│   │       ├── fxml/           # UI макеты
│   │       ├── images/         # Изображения товаров
│   │       └── hibernate.cfg.xml
│   └── test/
├── data/                        # Excel файлы с данными
├── database_schema.sql          # Скрипт создания БД
├── ER-diagram.pdf               # ER-диаграмма
├── pom.xml                      # Maven конфигурация
└── README.md
```


##  Выполненные требования

| Модуль | Требование | Статус |
| :-- | :-- | :-- |
| **Модуль 1** | 3НФ, внешние ключи, индексы | ГОТОВО
| **Модуль 1** | ER-диаграмма, скрипт БД |ГОТОВО
| **Модуль 1** | Импорт данных из Excel |ГОТОВО
| **Модуль 2** | Авторизация (4 роли) |ГОТОВО
| **Модуль 2** | Список товаров с подсветкой |ГОТОВО
| **Модуль 2** | Поиск, фильтрация, сортировка |ГОТОВО
| **Модуль 3** | CRUD товаров для админа |ГОТОВО
| **Модуль 3** | Загрузка фото (ресайз 300x200) |ГОТОВО
| **Модуль 3** | Валидация полей |ГОТОВО
| **Модуль 3** | Проверка наличия в заказах |
| **Модуль 3** | Просмотр заказов |


Выполнил Ревков Александр Максимович 22-23-Java



