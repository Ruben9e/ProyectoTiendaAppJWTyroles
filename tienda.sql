CREATE DATABASE IF NOT EXISTS tienda_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tienda_db;

CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    rol_id INT NOT NULL,
    CONSTRAINT fk_users_role FOREIGN KEY (rol_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL,
    category_id INT NOT NULL,
    seller_id INT NOT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_products_seller FOREIGN KEY (seller_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    client_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_client FOREIGN KEY (client_id) REFERENCES users (id),
    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES products (id)
);

INSERT IGNORE INTO roles (id, name) VALUES
    (1, 'ADMIN'), (2, 'CLIENTE'), (3, 'VENDEDOR');

-- Usuario inicial: admin / password. Cambia esta contraseña al iniciar el proyecto.
-- Hash BCrypt de "password" (coste 10).
INSERT IGNORE INTO users (username, email, password, active, rol_id)
VALUES ('admin', 'admin@tienda.local', '$2a$10$r5/QMJP57xBYEIkB0u6jiu5Hbb9YaVHLDpFVJ5pClcYofr0bHyc6i', TRUE, 1);
