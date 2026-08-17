CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.products
(
    product_id UUID PRIMARY KEY,
    fragile    BOOLEAN          NOT NULL DEFAULT FALSE,
    width      DOUBLE PRECISION NOT NULL,
    height     DOUBLE PRECISION NOT NULL,
    depth      DOUBLE PRECISION NOT NULL,
    weight     DOUBLE PRECISION NOT NULL,
    quantity   BIGINT           NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS warehouse.order_bookings
(
    order_id    UUID PRIMARY KEY,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS warehouse.order_booking_products
(
    order_id   UUID   NOT NULL REFERENCES warehouse.order_bookings (order_id),
    product_id UUID   NOT NULL,
    quantity   BIGINT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);
