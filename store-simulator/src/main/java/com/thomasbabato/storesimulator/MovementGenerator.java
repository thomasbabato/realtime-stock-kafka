package com.thomasbabato.storesimulator;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Creates one random stock movement for a store and saves it.
 */
@Component
public class MovementGenerator {

    private static final Logger log = LoggerFactory.getLogger(MovementGenerator.class);

    private final JdbcTemplate jdbc;
    private final SimulatorProperties props;

    public MovementGenerator(JdbcTemplate jdbc, SimulatorProperties props) {
        this.jdbc = jdbc;
        this.props = props;
    }

    public void generate(int storeId) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int productId = random.nextInt(1, props.products() + 1);
        int roll = random.nextInt(100);

        String type;
        int quantityChange;
        if (roll < 70) {
            type = "SALE";
            quantityChange = -random.nextInt(1, 6);        // -1 to -5
        } else if (roll < 90) {
            type = "DELIVERY";
            quantityChange = random.nextInt(10, 51);       // +10 to +50
        } else {
            type = "ADJUSTMENT";
            int amount = random.nextInt(1, 4);             // 1 to 3
            quantityChange = random.nextBoolean() ? amount : -amount;
        }

        jdbc.update("""
                INSERT INTO stock_movements (store_id, product_id, quantity_change, type)
                VALUES (?, ?, ?, ?)
                """, storeId, productId, quantityChange, type);

        log.debug("Store {}: {} product {} ({})", storeId, type, productId, quantityChange);
    }
}