DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

/*

        Товары и связанное

 */

CREATE TABLE countries (
    id serial PRIMARY KEY,
    name text UNIQUE NOT NULL
);

CREATE TABLE brands (
    id serial PRIMARY KEY,
    name text UNIQUE NOT NULL
);

CREATE TABLE glass_types (
    id serial PRIMARY KEY,
    type text UNIQUE NOT NULL
);

CREATE TABLE case_types (
    id serial PRIMARY KEY,
    type text UNIQUE NOT NULL
);

CREATE TABLE style_types (
    id serial PRIMARY KEY,
    type text UNIQUE NOT NULL
);

CREATE TABLE products (
    id serial PRIMARY KEY,
    name text NOT NULL,
    description text,
    country_id int REFERENCES countries(id),
    price NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    stock_quantity int CHECK(stock_quantity >= 0) DEFAULT 0,
    picture_url text,

    brand_id int REFERENCES brands(id),
    waterproof int CHECK (waterproof >= 0) DEFAULT 0,
    glass_type_id int REFERENCES glass_types(id),
    case_type_id int REFERENCES case_types(id),
    style_id int REFERENCES style_types(id)
);

/*

    Заказы

*/


CREATE TABLE order_statuses(
    id serial NOT NULL PRIMARY KEY,
    status text NOT NULL
);

CREATE TABLE orders(
    id serial PRIMARY KEY,
    status int REFERENCES order_statuses ON DELETE SET NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    confirmed_at TIMESTAMP WITHOUT TIME ZONE ,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    expected_delivery_at TIMESTAMP WITHOUT TIME ZONE,
    delivery_address text NOT NULL,
    contact_phone text NOT NULL,
    contact_name text NOT NULL,
    access_hash text NOT NULL
);

CREATE TABLE order_items(
    order_id int REFERENCES orders ON DELETE CASCADE,
    product_id int REFERENCES products ON DELETE CASCADE,
    quantity int CHECK ( quantity > 0 ),
    PRIMARY KEY (order_id, product_id)
);

-- Заполнение таблицы countries
INSERT INTO countries (name)
VALUES
    ('Швеция'),
    ('США'),
    ('Нидерланды'),
    ('Япония'),
    ('Корея');

-- Заполнение таблицы brands
INSERT INTO brands (name)
VALUES
    ('Casio'),
    ('Ника'),
    ('Seiko'),
    ('Omega'),
    ('Rolex'),
    ('Cartier'),
    ('Patek Philippe'),
    ('Tag Heuer'),
    ('Tissot'),
    ('Hamilton'),
    ('Longines'),
    ('Audemars Piguet');

-- Заполнение таблицы glass_types
INSERT INTO glass_types (type)
VALUES
    ('Минеральное стекло'),
    ('Сапфировое стекло'),
    ('Пластиковое стекло');

-- Заполнение таблицы case_types
INSERT INTO case_types (type)
VALUES
    ('Металлический корпус'),
    ('Пластиковый корпус'),
    ('Керамический корпус');

-- Заполнение таблицы style_types
INSERT INTO style_types (type)
VALUES
    ('Классический'),
    ('Спортивный'),
    ('Деловой');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Таблица ролей
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Связь пользователей и ролей (Many-to-Many)
CREATE TABLE user_roles (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    role_id INT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);



INSERT INTO products (name, description, country_id, price, stock_quantity, picture_url, brand_id, waterproof, glass_type_id, case_type_id, style_id)
VALUES
    ('Casio G-Shock', 'Ударопрочные спортивные часы.', 2, 99.99, 50, '1', 1, 200, 2, 1, 2),
    ('Seiko Presage', 'Классические механические часы.', 4, 499.99, 20, '2', 3, 50, 2, 1, 1),
    ('Omega Seamaster', 'Часы для дайвинга.', 1, 3299.99, 10, '3', 4, 300, 2, 1, 2),
    ('Rolex Submariner', 'Икона часов для дайвинга.', 1, 8999.99, 5, '4', 5, 300, 2, 1, 1),
    ('Cartier Tank', 'Элегантные деловые часы.', 2, 12999.99, 3, '5', 6, 30, 1, 1, 3),
    ('Tag Heuer Carrera', 'Спортивные часы с автоподзаводом.', 1, 2199.99, 12, '6', 8, 100, 2, 1, 2),
    ('Tissot Le Locle', 'Классические швейцарские часы.', 1, 699.99, 18, '7', 9, 30, 1, 1, 1),
    ('Hamilton Khaki Field', 'Военные часы с прочным корпусом.', 2, 599.99, 20, '8', 10, 50, 2, 1, 2),
    ('Longines Master Collection', 'Престижные часы с изысканным дизайном.', 3, 1399.99, 5, '9', 11, 30, 2, 1, 3),
    ('Audemars Piguet Royal Oak', 'Иконические часы в спортивном стиле.', 4, 21999.99, 2, '10', 12, 50, 1, 1, 2),

    ('Casio Pro Trek', 'Многофункциональные туристические часы.', 2, 299.99, 25, '11', 1, 100, 3, 2, 2),
    ('Seiko Astron', 'Часы с солнечной батареей.', 4, 1599.99, 10, '12', 3, 30, 2, 1, 3),
    ('Omega Constellation', 'Изысканные деловые часы.', 1, 6999.99, 8, '13', 4, 30, 2, 1, 3),
    ('Rolex Datejust', 'Эталон классики.', 1, 10999.99, 6, '14', 5, 100, 2, 1, 1),
    ('Cartier Santos', 'Элегантные часы с уникальным дизайном.', 2, 14999.99, 4, '15', 6, 30, 1, 1, 3),

    ('Tissot PRX', 'Модные часы с ретро-дизайном.', 3, 499.99, 30, '16', 9, 30, 2, 1, 2),
    ('Hamilton Ventura', 'Уникальные часы с треугольным корпусом.', 2, 1199.99, 15, '17', 10, 50, 3, 3, 3),
    ('Longines DolceVita', 'Классические часы с прямоугольным корпусом.', 1, 1299.99, 8, '18', 11, 30, 1, 3, 3),
    ('Audemars Piguet Millenary', 'Часы с асимметричным дизайном.', 4, 25999.99, 1, '19', 12, 30, 1, 1, 3),
    ('Tag Heuer Monaco', 'Спортивные часы с квадратным корпусом.', 3, 3599.99, 7, '20', 8, 50, 3, 3, 2);




INSERT INTO order_statuses (status)
VALUES
    ('Создан'),
    ('Подтвержден'),
    ('Отправлен'),
    ('Доставлен'),
    ('Отменен');


INSERT INTO orders (status, created_at, confirmed_at, completed_at, delivery_address, contact_phone, contact_name, access_hash)
VALUES
    (1, '2024-12-21 10:00:00', '2024-12-21 10:15:00', NULL, 'ул. Ленина, д. 10, кв. 5', '+71234567890','Карпенко Максим Дмитриевич', '1'),
    (2, '2024-12-20 14:30:00', '2024-12-20 14:45:00', '2024-12-21 12:00:00', 'ул. Победы, д. 15, кв. 23', '+79876543210', 'Карпенко Максим Дмитриевич','2');


INSERT INTO order_items (order_id, product_id, quantity)
VALUES
    (1, 1, 2),
    (1, 2, 1),
    (2, 3, 1),
    (2, 4, 1);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_OPERATOR');

-- Пользователи (пароли должны быть захэшированы)
INSERT INTO users (username, password) VALUES
                                           ('admin', '$2a$10$DMQUo.pbjpFYC0Sfm9HAjuoAA1i0BAcagS5oy3CixhnCv9ApVlNEa'),
                                           ('user', '$2a$10$DMQUo.pbjpFYC0Sfm9HAjuoAA1i0BAcagS5oy3CixhnCv9ApVlNEa');

-- Связь пользователей и ролей
INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 1),
                                              (2, 2);