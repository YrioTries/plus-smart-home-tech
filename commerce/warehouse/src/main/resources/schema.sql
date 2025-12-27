CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id UUID              NOT NULL,
    fragile    BOOLEAN           NOT NULL,
    width      DOUBLE  PRECISION NOT NULL,
    height     DOUBLE  PRECISION NOT NULL,
    depth      DOUBLE  PRECISION NOT NULL,
    weight     DOUBLE  PRECISION NOT NULL,
    quantity   INTEGER           NOT NULL,
    CONSTRAINT pk_warehouse_products PRIMARY KEY (product_id)
);