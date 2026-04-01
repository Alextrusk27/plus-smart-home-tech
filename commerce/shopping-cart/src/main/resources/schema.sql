CREATE TABLE IF NOT EXISTS carts (
    shopping_cart_id UUID PRIMARY KEY,
    username         VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_products (
    shopping_cart_id UUID REFERENCES carts (shopping_cart_id) ON DELETE CASCADE,
    product_id       UUID NOT NULL,
    quantity         INT  NOT NULL,
    PRIMARY KEY (shopping_cart_id, product_id),
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
);