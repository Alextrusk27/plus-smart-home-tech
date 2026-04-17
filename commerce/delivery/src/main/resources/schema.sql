CREATE TABLE IF NOT EXISTS delivery (
    id                   UUID         PRIMARY KEY,
    order_id             UUID         NOT NULL,
    state                VARCHAR(15)  NOT NULL,
    fragile              BOOL         NOT NULL,
    weight               DECIMAL(6,2) NOT NULL,
    volume               DECIMAL(6,2) NOT NULL,
    sender_address_id    BIGINT NOT NULL REFERENCES address (id) ON DELETE RESTRICT,
    recipient_address_id BIGINT NOT NULL REFERENCES address (id) ON DELETE RESTRICT,

    CONSTRAINT chk_delivery_state_valid CHECK (state IN ('CREATED', 'IN_PROGRESS', 'DELIVERED', 'FAILED', 'CANCELLED')),
    CONSTRAINT chk_delivery_weight_greater_than_zero CHECK (weight > 0),
    CONSTRAINT chk_delivery_volume_greater_than_zero CHECK (volume > 0),
    CONSTRAINT chk_sender_and_recipient_not_equal    CHECK (sender_address_id != recipient_address_id)
);

CREATE TABLE IF NOT EXISTS address (
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    country VARCHAR(100) NOT NULL,
    city    VARCHAR(100) NOT NULL,
    street  VARCHAR(200) NOT NULL,
    house   VARCHAR(20)  NOT NULL,
    flat    VARCHAR(20)  NOT NULL,

    CONSTRAINT uk_address            UNIQUE (country, city, street, house, flat),
    CONSTRAINT chk_country_not_empty CHECK (LENGTH(country) > 0),
    CONSTRAINT chk_city_not_empty    CHECK (LENGTH(city) > 0),
    CONSTRAINT chk_street_not_empty  CHECK (LENGTH(street) > 0),
    CONSTRAINT chk_house_not_empty   CHECK (LENGTH(house) > 0)
);