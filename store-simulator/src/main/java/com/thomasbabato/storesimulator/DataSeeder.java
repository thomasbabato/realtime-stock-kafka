package com.thomasbabato.storesimulator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Fills the store database with stores and products on first start.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final List<String> CITIES = List.of(
            "Milano", "Roma", "Torino", "Napoli", "Bologna",
            "Firenze", "Venezia", "Verona", "Genova", "Bari",
            "Padova", "Trieste", "Brescia", "Parma", "Modena",
            "Bergamo", "Pisa", "Trento", "Udine", "Treviso");

    private static final List<String> CATEGORIES = List.of(
            "Dairy", "Bakery", "Beverages", "Fruit and vegetables",
            "Pantry", "Frozen", "Household");

    private final JdbcTemplate jdbc;
    private final SimulatorProperties props;

    public DataSeeder(JdbcTemplate jdbc, SimulatorProperties props) {
        this.jdbc = jdbc;
        this.props = props;
    }

    @Override
    public void run(String... args) {
        Integer existing = jdbc.queryForObject("SELECT COUNT(*) FROM stores", Integer.class);
        if (existing != null && existing > 0) {
            log.info("Database already seeded ({} stores), skipping", existing);
            return;
        }

        for (int i = 1; i <= props.stores(); i++) {
            String city = CITIES.get((i - 1) % CITIES.size());
            jdbc.update("INSERT INTO stores (name, city) VALUES (?, ?)",
                    "Store %02d".formatted(i), city);
        }

        for (int i = 1; i <= props.products(); i++) {
            String category = CATEGORIES.get((i - 1) % CATEGORIES.size());
            jdbc.update("INSERT INTO products (name, category) VALUES (?, ?)",
                    "%s item %03d".formatted(category, i), category);
        }

        log.info("Seeded {} stores and {} products", props.stores(), props.products());
    }
}