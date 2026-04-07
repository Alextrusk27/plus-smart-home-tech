CREATE TABLE IF NOT EXISTS products(
    product_id UUID         PRIMARY KEY,
    fragile    BOOL         NOT NULL,
    width      DECIMAL(6,2) NOT NULL,
    height     DECIMAL(6,2) NOT NULL,
    depth      DECIMAL(6,2) NOT NULL,
    weight     DECIMAL(6,2) NOT NULL,
    quantity   INT          NOT NULL,

    CONSTRAINT chk_width_greater_than_zero  CHECK (width > 0),
    CONSTRAINT chk_height_greater_than_zero CHECK (height > 0),
    CONSTRAINT chk_depth_greater_than_zero  CHECK (depth > 0),
    CONSTRAINT chk_weight_greater_than_zero CHECK (weight > 0),
    CONSTRAINT chk_quantity_positive        CHECK (quantity >= 0)
);