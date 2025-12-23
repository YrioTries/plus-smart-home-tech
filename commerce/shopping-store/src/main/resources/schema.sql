CREATE TABLE IF NOT EXISTS products (
    id               VARCHAR(36)   NOT NULL,
    name             VARCHAR(40)   NOT NULL,
    description      TEXT          NOT NULL,
    imageSrc         VARCHAR(500),
    quantity_state   VARCHAR(10)   NOT NULL,
    product_state    VARCHAR(10)   NOT NULL,
    product_category VARCHAR(20)   NOT NULL,
    price            INTEGER       NOT NULL,
    CONSTRAINT pk_p PRIMARY KEY (id)
);