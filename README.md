
# 📝 Создайте файл README.md в корне проекта

Файл: C:\Users\AlexGG\IdeaProjects\ShoeStore\README.md
markdown

# ShoeStore - Информационная система магазина обуви

![Java](https://img.shields.io/badge/Java-24-007396?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-007396?style=for-the-badge&logo=java)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.4-336791?style=for-the-badge&logo=postgresql)
![Hibernate](https://img.shields.io/badge/Hibernate-6.4.4-59666C?style=for-the-badge&logo=hibernate)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven)

## 📋 Описание проекта

Информационная система для магазина обуви "ООО Обувь". Разработана в рамках демонстрационного экзамена по компетенции "Программные решения для бизнеса".

Система позволяет:

- Авторизовываться пользователям с разными ролями (админ, менеджер, клиент, гость)
- Просматривать товары с подсветкой в зависимости от скидки и наличия
- Искать, фильтровать и сортировать товары
- Управлять товарами (добавление, редактирование, удаление) для администратора
- Загружать и изменять изображения товаров


## 🛠 Технологический стек

| Компонент | Технология |
| :-- | :-- |
| Язык программирования | Java 24 |
| GUI | JavaFX 21 |
| ORM | Hibernate 6.4.4 |
| База данных | PostgreSQL 42.7.4 |
| Сборщик | Maven |
| Работа с Excel | Apache POI 5.2.5 |
| Хеширование паролей | jBCrypt 0.4 |

## 📊 Структура базы данных

┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│ users │ │ orders │ │pickup_points│
├─────────────┤ ├─────────────┤ ├─────────────┤
│ id (PK) │<─────│ user_id │ │ id (PK) │
│ full_name │ │ order_number│ │ address │
│ login (UQ) │ │ order_date │ └─────────────┘
│ password │ │ delivery_date│ ▲
│ role │ │ pickup_point_id│───────┘
└─────────────┘ │ pickup_code │ ┌─────────────┐
│ status │ │ products │
└─────────────┘ ├─────────────┤
│ │ article(PK) │
▼ │ name │
┌─────────────┐ │ price │
│ order_items │ │ discount │
├─────────────┤ │ stock │
│ id (PK) │ │ ... │
│ order_id │─────>│ photo_path │
│ product_article│───>└─────────────┘
│ quantity │
│ price_at_order│
└─────────────┘
text

## 👥 Роли пользователей

| Роль | Права |
| :-- | :-- |
| **Гость** | Просмотр товаров без фильтрации |
| **Клиент** | Просмотр товаров без фильтрации |
| **Менеджер** | Просмотр товаров с поиском, фильтрацией, сортировкой, просмотр заказов (в разработке) |
| **Администратор** | Полный доступ: CRUD товаров, поиск, фильтрация, сортировка, управление заказами (в разработке) |

## 🔐 Учётные записи для входа

| Роль | Логин | Пароль |
| :-- | :-- | :-- |
| **Администратор** | `94d5ous@gmail.com` | `uzWC67` |
| **Администратор** | `uth4iz@mail.com` | `2L6KZG` |
| **Администратор** | `yzls62@outlook.com` | `JlFRCZ` |
| **Менеджер** | `1diph5e@tutanota.com` | `8ntwUp` |
| **Менеджер** | `tjde7c@yahoo.com` | `YOyhfR` |
| **Менеджер** | `wpmrc3do@tutanota.com` | `RSbvHv` |
| **Клиент** | `5d4zbu@tutanota.com` | `rwVDh9` |
| **Клиент** | `ptec8ym@yahoo.com` | `LdNyos` |
| **Клиент** | `1qz4kw@mail.com` | `gynQMT` |
| **Клиент** | `4np6se@mail.com` | `AtnDjr` |

## 🚀 Установка и запуск

### Предварительные требования

- JDK 24 или выше
- PostgreSQL 14 или выше
- Maven 3.9+
- IntelliJ IDEA (рекомендуется)


### Шаг 1: Клонирование репозитория

```bash
git clone https://github.com/ваш-логин/ShoeStore.git
cd ShoeStore
Шаг 2: Настройка базы данных
Создайте базу данных в PostgreSQL:
sql
CREATE DATABASE shoestore;
Настройте пароль в файле src/main/resources/hibernate.cfg.xml:
xml
<property name="hibernate.connection.password">1234</property>
Выполните скрипт создания таблиц:
bash
psql -U postgres -d shoestore -f database_schema.sql
Шаг 3: Импорт данных (опционально)
Если нужно заполнить базу тестовыми данными:
bash
# Запустите импортёры в IDEA или выполните:
mvn exec:java -Dexec.mainClass="com.shoestore.importer.UserImporter"
mvn exec:java -Dexec.mainClass="com.shoestore.importer.PickupPointImporter"
mvn exec:java -Dexec.mainClass="com.shoestore.importer.ProductImporter"
mvn exec:java -Dexec.mainClass="com.shoestore.importer.OrderImporter"
Шаг 4: Сборка и запуск
bash
# Сборка проекта
mvn clean package

# Запуск через Maven
mvn javafx:run

# Или запуск скомпилированного JAR
java --module-path "C:\Users\AlexGG\.m2\repository\org\openjfx\javafx-base\21.0.2;C:\Users\AlexGG\.m2\repository\org\openjfx\javafx-controls\21.0.2;C:\Users\AlexGG\.m2\repository\org\openjfx\javafx-graphics\21.0.2;C:\Users\AlexGG\.m2\repository\org\openjfx\javafx-fxml\21.0.2" --add-modules javafx.controls,javafx.fxml -jar target/shoe-store-1.0.0.jar
Запуск в IntelliJ IDEA
Откройте проект в IDEA
Настройте VM options в конфигурации запуска:
text
--module-path "C:\Users\AlexGG\.m2\repository\org\openjfx\javafx-base\21.0.2;C:\Users\AlexGG\.m2\repository\org\openjfx\javafx-controls\21.0.2;C:\Users\AlexGG\.m2\repository\org\openjfx\javafx-graphics\21.0.2;C:\Users\AlexGG\.m2\repository\org\openjfx\javafx-fxml\21.0.2" --add-modules javafx.controls,javafx.fxml
Запустите класс com.shoestore.ShoeStoreApp
🎨 Особенности интерфейса
Шрифт: Times New Roman на всех формах
Цветовая схема:
Основной фон: #FFFFFF (белый)
Дополнительный фон: #7FFF00 (кнопки второстепенных действий)
Акцентирование: #00FA9A (кнопки сохранения)
Скидка >15%: #2E8B57 (зелёный фон строки)
Нет в наличии: #ADD8E6 (голубой фон строки)
Цена со скидкой: красный цвет, перечёркнуто
📁 Структура проекта
text
ShoeStore/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/shoestore/
│   │   │       ├── controller/     # Контроллеры JavaFX
│   │   │       ├── dao/            # Доступ к данным
│   │   │       ├── importer/        # Импорт из Excel
│   │   │       ├── model/           # JPA сущности
│   │   │       ├── util/            # Утилиты
│   │   │       └── ShoeStoreApp.java # Главный класс
│   │   └── resources/
│   │       ├── fxml/                # FXML файлы интерфейса
│   │       ├── images/              # Изображения
│   │       └── hibernate.cfg.xml    # Конфигурация Hibernate
│   └── test/                        # Тесты
├── data/                             # Excel файлы с данными
├── database_schema.sql               # Скрипт создания БД
├── pom.xml                           # Maven конфигурация
└── README.md                         # Этот файл
✅ Выполненные требования
Модуль 1 (База данных)
Таблицы в 3НФ
Внешние ключи и индексы
ER-диаграмма
Импорт данных из Excel
Скрипт БД
Модуль 2 (Приложение)
Авторизация (админ, менеджер, клиент, гость)
Список товаров с подсветкой
Поиск, фильтрация, сортировка
Соответствие руководству по стилю
Модуль 3 (CRUD)
Добавление товара (админ)
Редактирование товара (админ)
Удаление товара (админ)
Загрузка фото (ресайз 300x200)
Валидация полей
Проверка на наличие в заказах перед удалением
Просмотр заказов (в разработке)
📄 Лицензия

Вот улучшенная и дополненная версия файла README.md для вашего проекта ShoeStore. Я исправил форматирование, добавил недостающие разделы и сделал документацию более профессиональной.

***

# ShoeStore - Информационная система магазина обуви





## 📋 Описание проекта

Информационная система для магазина обуви "ООО Обувь". Разработана в рамках **демонстрационного экзамена** по компетенции **"Программные решения для бизнеса"**.

**Основные возможности:**
- 🔐 Авторизация пользователей с 4 ролями (админ, менеджер, клиент, гость)
- 🛍️ Просмотр товаров с визуальной подсветкой (скидки, наличие)
- 🔍 Поиск, фильтрация и сортировка товаров
- ⚙️ CRUD операции с товарами для администратора
- 🖼️ Загрузка и изменение изображений товаров (ресайз 300x200)
- 📋 Управление заказами (в разработке)

## 🛠 Технологический стек

| Компонент | Технология | Версия |
|-----------|------------|--------|
| Язык программирования | Java | 24 |
| GUI | JavaFX | 21 |
| ORM | Hibernate | 6.4.4 |
| База данных | PostgreSQL | 42.7.4 |
| Сборщик | Maven | 3.9 |
| Excel | Apache POI | 5.2.5 |
| Хеширование | jBCrypt | 0.4 |

## 📊 Структура базы данных

```mermaid
erDiagram
    USERS ||--o{ ORDER_ITEMS : "makes"
    USERS ||--o{ ORDERS : "places"
    ORDERS ||--o{ ORDER_ITEMS : "contains"
    PRODUCTS ||--o{ ORDER_ITEMS : "included_in"
    PICKUP_POINTS ||--o{ ORDERS : "has"
    
    USERS {
        int id PK
        string full_name
        string login UQ
        string password
        string role
    }
    
    ORDERS {
        int id PK
        int user_id FK
        string order_number
        date order_date
        date delivery_date
        int pickup_point_id FK
        string pickup_code
        string status
    }
    
    ORDER_ITEMS {
        int id PK
        int order_id FK
        string product_article FK
        int quantity
        double price_at_order
    }
    
    PRODUCTS {
        string article PK
        string name
        double price
        int discount
        int stock
        string photo_path
    }
    
    PICKUP_POINTS {
        int id PK
        string address
    }
```


## 👥 Роли пользователей

| Роль | Права доступа |
| :-- | :-- |
| **🆓 Гость** | Просмотр товаров |
| **👤 Клиент** | Просмотр товаров |
| **🧑‍💼 Менеджер** | Поиск, фильтрация, сортировка, просмотр заказов |
| **⚙️ Администратор** | Полный CRUD товаров + все права менеджера |

## 🔐 Тестовые учетные записи

| Роль | Логин | Пароль |
| :-- | :-- | :-- |
| **Администратор** | `94d5ous@gmail.com` | `uzWC67` |
| **Администратор** | `uth4iz@mail.com` | `2L6KZG` |
| **Менеджер** | `1diph5e@tutanota.com` | `8ntwUp` |
| **Клиент** | `5d4zbu@tutanota.com` | `rwVDh9` |

> **⚠️ В продакшене смените пароли!**

## 🚀 Быстрый старт

### 1. Предварительные требования

```bash
# Установите:
JDK 24+
PostgreSQL 14+
Maven 3.9+
IntelliJ IDEA (рекомендуется)
```


### 2. Клонирование и настройка БД

```bash
git clone https://github.com/ваш-логин/ShoeStore.git
cd ShoeStore

# Создайте БД
psql -U postgres -c "CREATE DATABASE shoestore;"

# Настройте пароль в hibernate.cfg.xml
# Выполните схему БД
psql -U postgres -d shoestore -f database_schema.sql
```


### 3. Импорт тестовых данных

```bash
mvn exec:java -Dexec.mainClass="com.shoestore.importer.UserImporter"
mvn exec:java -Dexec.mainClass="com.shoestore.importer.ProductImporter"
```


### 4. Запуск

```bash
# Maven
mvn clean javafx:run

# JAR (Windows)
java --module-path "$PATH_TO_JAVAFX_LIBS" --add-modules javafx.controls,javafx.fxml -jar target/shoe-store-1.0.0.jar
```


## 🎨 UI/UX Особенности

- **Шрифт:** Times New Roman (все формы)
- **Цветовая схема:**


| Элемент | Цвет | HEX |
| :-- | :-- | :-- |
| Фон | Белый | `#FFFFFF` |
| Скидка >15% | Зеленый | `#2E8B57` |
| Нет в наличии | Голубой | `#ADD8E6` |
| Цена со скидкой | Красный, зачеркнутая |  |


## 📁 Структура проекта

```
ShoeStore/
├── src/main/java/com/shoestore/
│   ├── controller/     # JavaFX контроллеры
│   ├── dao/           # Репозитории
│   ├── model/         # JPA Entity
│   ├── importer/      # Excel импорт
│   └── ShoeStoreApp.java
├── src/main/resources/
│   ├── fxml/          # UI макеты
│   ├── images/        # Фото товаров
│   └── hibernate.cfg.xml
├── data/              # Исходные Excel
├── pom.xml
└── database_schema.sql
```


## ✅ Выполненные требования экзамена

| Модуль | Требование | ✅ Статус |
| :-- | :-- | :-- |
| **Модуль 1** | 3НФ, FK, индексы, ERD | ✅ |
| **Модуль 1** | Импорт Excel → БД | ✅ |
| **Модуль 2** | 4 роли авторизации | ✅ |
| **Модуль 2** | Подсветка товаров | ✅ |
| **Модуль 3** | CRUD + фото (ресайз) | ✅ |
| **Модуль 3** | Валидация форм | ✅ |

## 📄 Лицензия

```
MIT License
Copyright (c) 2026 Ваш_Имя

Разрешено использование, копирование, модификация в любых целях.
```


***

**Готово для копирования в `C:\Users\AlexGG\IdeaProjects\ShoeStore\README.md`!**

Хотите добавить скриншоты интерфейса или изменить какие-то разделы под ваши конкретные требования экзамена?

