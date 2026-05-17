CREATE SCHEMA IF NOT EXISTS shopping_store;
CREATE TABLE IF NOT EXISTS shopping_store.products
(
    product_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name     VARCHAR(255)   NOT NULL,
    description      TEXT           NOT NULL,
    image_src        VARCHAR(512),
    price            DECIMAL(10, 2) NOT NULL,
    quantity_state   VARCHAR(50)    NOT NULL,
    product_state    VARCHAR(50)    NOT NULL,
    product_category VARCHAR(50)    NOT NULL
);