CREATE TABLE IF NOT EXISTS shopping_carts (
    id VARCHAR(36) NOT NULL,
    state VARCHAR(10) NOT NULL,
    owner VARCHAR(20) NOT NULL,
    CONSTRAINT pk_sc PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(40) NOT NULL,
    description TEXT NOT NULL,
    imageSrc VARCHAR(500),
    quantity_state VARCHAR(10) NOT NULL,
    product_state VARCHAR(10) NOT NULL,
    product_category VARCHAR(20) NOT NULL,
    price INTEGER NOT NULL,
    CONSTRAINT pk_p PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products_cart (
    cart_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT pk_pc PRIMARY KEY (cart_id, product_id),
    CONSTRAINT fk_cart_id FOREIGN KEY (cart_id) REFERENCES shopping_carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);