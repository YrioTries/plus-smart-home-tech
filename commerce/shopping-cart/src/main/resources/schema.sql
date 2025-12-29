CREATE TABLE IF NOT EXISTS shopping_carts (
    id    UUID        NOT NULL,
    state VARCHAR(10) NOT NULL,
    owner VARCHAR(20) NOT NULL,
    CONSTRAINT pk_sc PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products_cart (
    cartId    UUID NOT NULL,
    productId UUID NOT NULL,
    quantity   INT  NOT NULL DEFAULT 1,
    CONSTRAINT pk_pc PRIMARY KEY (cart_id, product_id),
    CONSTRAINT fk_cart_id FOREIGN KEY (cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE
);