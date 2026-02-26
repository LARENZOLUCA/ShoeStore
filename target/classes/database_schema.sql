CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       full_name VARCHAR(255) NOT NULL,
                       login VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE products (
                          article VARCHAR(50) PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          unit VARCHAR(50) NOT NULL,
                          price DECIMAL(10,2) NOT NULL,
                          supplier VARCHAR(255) NOT NULL,
                          manufacturer VARCHAR(255) NOT NULL,
                          category VARCHAR(100) NOT NULL,
                          discount INTEGER NOT NULL,
                          stock_quantity INTEGER NOT NULL,
                          description TEXT,
                          photo_path VARCHAR(500)
);

CREATE TABLE pickup_points (
                               id BIGSERIAL PRIMARY KEY,
                               address TEXT UNIQUE NOT NULL
);

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_number VARCHAR(50) NOT NULL,
                        order_date TIMESTAMP NOT NULL,
                        delivery_date TIMESTAMP,
                        pickup_point_id BIGINT REFERENCES pickup_points(id),
                        user_id BIGINT REFERENCES users(id),
                        pickup_code VARCHAR(50),
                        status VARCHAR(50) NOT NULL
);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_article VARCHAR(50) NOT NULL REFERENCES products(article),
                             quantity INTEGER NOT NULL,
                             price_at_order DECIMAL(10,2) NOT NULL
);

-- Индексы
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_pickup_point ON orders(pickup_point_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_article);