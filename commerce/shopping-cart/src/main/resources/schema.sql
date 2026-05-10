CREATE SCHEMA IF NOT EXISTS shopping_cart;

CREATE TABLE IF NOT EXISTS shopping_cart.carts
(
    shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username         VARCHAR(255) NOT NULL,
    active           BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS shopping_cart.shopping_cart_items
(
    cart_id    UUID   NOT NULL REFERENCES shopping_cart.carts (shopping_cart_id) ON DELETE CASCADE,
    product_id UUID   NOT NULL,
    quantity   BIGINT NOT NULL,
    PRIMARY KEY (cart_id, product_id)
);
