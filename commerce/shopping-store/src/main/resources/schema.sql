CREATE TABLE IF NOT EXISTS products (
    product_id       UUID           PRIMARY KEY,
    product_name     VARCHAR(255)   NOT NULL,
    description      TEXT           NOT NULL,
    image_src        VARCHAR(500)   NOT NULL,
    quantity_state   VARCHAR(20)    NOT NULL,
    product_state    VARCHAR(20)    NOT NULL,
    product_category VARCHAR(50)    NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,

    CONSTRAINT chk_quantity_state   CHECK (quantity_state   IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    CONSTRAINT chk_product_state    CHECK (product_state    IN ('ACTIVE', 'DEACTIVATE')),
    CONSTRAINT chk_product_category CHECK (product_category IN ('LIGHTING', 'CONTROL', 'SENSORS')),
    CONSTRAINT chk_price_positive   CHECK (price >= 0)
);