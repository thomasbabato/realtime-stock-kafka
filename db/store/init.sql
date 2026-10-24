CREATE TABLE stores (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL
);

-- Rows are only inserted, never deleted, like that the connector can pick up new rows based on id
CREATE TABLE stock_movements (
    id BIGSERIAL PRIMARY KEY,
    store_id INT NOT NULL REFERENCES stores(id),
    product_id INT NOT NULL REFERENCES products(id),
    quantity_change INT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN('SALE', 'DELIVERY', 'ADJUSTMENT')),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);