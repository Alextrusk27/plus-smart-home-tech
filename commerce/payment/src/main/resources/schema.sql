CREATE TABLE IF NOT EXISTS payments (
    payment_id     UUID PRIMARY KEY,
    total_payment  DECIMAL(6,2),
    products_total DECIMAL(6,2),
    delivery_total DECIMAL(6,2),
    state          VARCHAR(10),

    CONSTRAINT chk_total_payment_positive  CHECK (total_payment > 0),
    CONSTRAINT chk_products_total_positive CHECK (products_total > 0),
    CONSTRAINT chk_delivery_total_positive CHECK (delivery_total > 0),
    CONSTRAINT chk_state_valid             CHECK (state IN ('PENDING', 'SUCCESS', 'FAILED'))
);