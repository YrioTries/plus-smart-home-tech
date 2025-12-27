CREATE TABLE IF NOT EXISTS products (
    id               UUID          NOT NULL,
    name             VARCHAR(40)   NOT NULL,
    description      VARCHAR(200)  NOT NULL,
    imageSrc         VARCHAR(500),
    quantity_state   VARCHAR(10)   NOT NULL,
    product_state    VARCHAR(10)   NOT NULL,
    product_category VARCHAR(20)   NOT NULL,
    price            NUMERIC(10,2) NOT NULL,
    CONSTRAINT pk_p PRIMARY KEY (id)
);