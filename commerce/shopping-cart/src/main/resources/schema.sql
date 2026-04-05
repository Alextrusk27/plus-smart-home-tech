CREATE TABLE IF NOT EXISTS carts (
    shopping_cart_id UUID         PRIMARY KEY,
    username         VARCHAR(255) NOT NULL,
    is_active        BOOL         DEFAULT TRUE NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_products (
    shopping_cart_id UUID REFERENCES carts (shopping_cart_id) ON DELETE CASCADE,
    product_id       UUID NOT NULL,
    quantity         INT  NOT NULL,
    PRIMARY KEY (shopping_cart_id, product_id),
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
);

CREATE UNIQUE INDEX idx_unique_active_cart_per_user
ON carts (username)
WHERE is_active = true;

CREATE OR REPLACE FUNCTION check_cart_active()
RETURNS TRIGGER AS
'
DECLARE
    cart_active BOOLEAN;
BEGIN
    SELECT is_active INTO cart_active
    FROM carts
    WHERE shopping_cart_id = NEW.shopping_cart_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION ''Cart % does not exist'', NEW.shopping_cart_id;
    END IF;

    IF cart_active = FALSE THEN
        RAISE EXCEPTION ''Cart % is inactive. Cannot modify products'', NEW.shopping_cart_id;
    END IF;

    RETURN NEW;
END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_check_cart_active ON cart_products;

CREATE TRIGGER trigger_check_cart_active
BEFORE INSERT OR UPDATE ON cart_products
FOR EACH ROW
EXECUTE FUNCTION check_cart_active();