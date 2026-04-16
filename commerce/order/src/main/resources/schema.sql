CREATE TABLE IF NOT EXISTS orders (
    order_id         UUID PRIMARY KEY,
    username         VARCHAR(255) NOT NULL,
    shopping_cart_id UUID NOT NULL UNIQUE,
    payment_id       UUID,
    delivery_id      UUID,
    state            VARCHAR(20) NOT NULL,
    delivery_weight  DECIMAL(6,2) NOT NULL,
    delivery_volume  DECIMAL(6,2) NOT NULL,
    delivery_price   DECIMAL(6,2) NOT NULL,
    fragile          BOOL NOT NULL,
    product_price    DECIMAL(6,2) NOT NULL,
    total_price      DECIMAL(6,2) NOT NULL,

    CONSTRAINT chk_state CHECK (state IN ('NEW', 'ON_PAYMENT', 'ON_DELIVERY', 'ON_ASSEMBLY', 'DELIVERED', 'ASSEMBLED',
        'PAID', 'COMPLETED', 'DELIVERY_FAILED', 'ASSEMBLY_FAILED', 'PAYMENT_FAILED', 'PRODUCT_RETURNED', 'CANCELED')),

    CONSTRAINT chk_delivery_weight_than_zero CHECK (delivery_weight > 0),
    CONSTRAINT chk_delivery_volume_than_zero CHECK (delivery_volume > 0),
    CONSTRAINT chk_delivery_price_than_zero  CHECK (delivery_price > 0),
    CONSTRAINT chk_product_price_than_zero   CHECK (product_price > 0),
    CONSTRAINT chk_total_price_than_zero     CHECK (total_price > 0)
);

CREATE TABLE IF NOT EXISTS order_products (
   order_id         UUID REFERENCES orders (order_id) ON DELETE RESTRICT,
   product_id       UUID NOT NULL,
   quantity         INT  NOT NULL,

   PRIMARY KEY (order_id, product_id),
   CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
);

CREATE UNIQUE INDEX idx_unique_payment_id ON orders(payment_id)
WHERE payment_id IS NOT NULL;

CREATE UNIQUE INDEX idx_unique_delivery_id ON orders(delivery_id)
WHERE delivery_id IS NOT NULL;