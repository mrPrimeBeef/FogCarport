package app.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractMapperTest {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "fog";

    protected static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeEach
    public void setUpDatabase() {
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {

            // Drop tables and sequences
            stmt.execute("DROP TABLE IF EXISTS orderline CASCADE");
            stmt.execute("DROP TABLE IF EXISTS orderr CASCADE");
            stmt.execute("DROP TABLE IF EXISTS account CASCADE");
            stmt.execute("DROP TABLE IF EXISTS item CASCADE");
            stmt.execute("DROP TABLE IF EXISTS zip_code CASCADE");

            // Create tables
            stmt.execute("""
                        CREATE TABLE zip_code (
                            zip_code INTEGER NOT NULL,
                            city CHARACTER VARYING,
                            CONSTRAINT zip_code_pkey PRIMARY KEY (zip_code)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE account (
                            account_id SERIAL NOT NULL,
                            email CHARACTER VARYING(64) NOT NULL,
                            password CHARACTER VARYING(64) NOT NULL,
                            name CHARACTER VARYING,
                            role CHARACTER VARYING DEFAULT 'Kunde',
                            address CHARACTER VARYING,
                            zip_code INTEGER,
                            phone CHARACTER VARYING,
                            CONSTRAINT account_pkey PRIMARY KEY (account_id),
                            CONSTRAINT email_unique UNIQUE (email)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE item (
                            item_id SERIAL NOT NULL,
                            name CHARACTER VARYING NOT NULL,
                            description CHARACTER VARYING,
                            item_type CHARACTER VARYING,
                            material_type CHARACTER VARYING,
                            length_cm NUMERIC,
                            width_mm NUMERIC,
                            height_mm NUMERIC,
                            package_amount INTEGER,
                            package_type CHARACTER VARYING,
                            cost_price NUMERIC NOT NULL,
                            CONSTRAINT item_pkey PRIMARY KEY (item_id)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE orderr (
                            orderr_id SERIAL NOT NULL,
                            date_placed DATE DEFAULT CURRENT_DATE,
                            date_completed DATE,
                            account_id INTEGER NOT NULL,
                            status CHARACTER VARYING NOT NULL,
                            paid BOOLEAN DEFAULT FALSE,
                            date_paid DATE,
                            margin_percentage NUMERIC,
                            carport_length_cm INTEGER NOT NULL,
                            carport_width_cm INTEGER NOT NULL,
                            carport_height_cm INTEGER,
                            shed_length_cm INTEGER,
                            shed_width_cm INTEGER,
                            svg_side_view CHARACTER VARYING,
                            svg_top_view CHARACTER VARYING,
                            CONSTRAINT orders_pkey PRIMARY KEY (orderr_id)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE orderline (
                            orderline_id SERIAL NOT NULL,
                            item_id INTEGER NOT NULL,
                            quantity INTEGER NOT NULL,
                            orderr_id INTEGER NOT NULL,
                            cost_price NUMERIC NOT NULL,
                            CONSTRAINT orderline_pkey PRIMARY KEY (orderline_id)
                        )
                    """);

            // Add foreign key constraints
            stmt.execute("""
                        ALTER TABLE account
                        ADD CONSTRAINT fk_zip FOREIGN KEY (zip_code)
                        REFERENCES zip_code (zip_code) MATCH SIMPLE
                        ON UPDATE NO ACTION
                        ON DELETE NO ACTION
                    """);

            stmt.execute("""
                        ALTER TABLE orderline
                        ADD CONSTRAINT fk_item_id FOREIGN KEY (item_id)
                        REFERENCES item (item_id) MATCH SIMPLE
                        ON UPDATE NO ACTION
                        ON DELETE NO ACTION
                    """);

            stmt.execute("""
                        ALTER TABLE orderline
                        ADD CONSTRAINT fk_orderr_id FOREIGN KEY (orderr_id)
                        REFERENCES orderr (orderr_id) MATCH SIMPLE
                        ON UPDATE NO ACTION
                        ON DELETE NO ACTION
                    """);

            stmt.execute("""
                        ALTER TABLE orderr
                        ADD CONSTRAINT fk FOREIGN KEY (account_id)
                        REFERENCES account (account_id) MATCH SIMPLE
                        ON UPDATE NO ACTION
                        ON DELETE NO ACTION
                    """);

            // Insert initial test data
            stmt.execute("""
                        INSERT INTO zip_code (zip_code, city) VALUES
                            (2100, 'København Ø'),
                            (2200, 'København N'),
                            (2300, 'København S'),
                            (2400, 'København NV')
                    """);

            stmt.execute("""
                        INSERT INTO account (email, password, name, role, address, zip_code, phone) VALUES
                            ('test@test.dk', '1234', 'Test Testersen', 'Kunde', 'Testvej 1', 2100, '12345678'),
                            ('admin@admin.dk', 'admin', 'Admin Admin', 'salesrep', 'Adminvej 1', 2200, '87654321'),
                            ('kunde@kunde.dk', 'kunde', 'Kurt Kunde', 'Kunde', 'Kundevej 123', 2300, '11223344')
                    """);

            stmt.execute("""
                        INSERT INTO item (name, description, item_type, material_type, length_cm, width_mm, height_mm, package_amount, package_type, cost_price) VALUES
                            ('Stolpe', '97x97 mm trykimp.', 'Byggemateriale', 'Træ', 300, 97, 97, 1, 'Stk.', 120),
                            ('Spærtræ', '45x195 mm ubh.', 'Byggemateriale', 'Træ', 600, 45, 195, 1, 'Stk.', 75)
                    """);

            stmt.execute("""
                        INSERT INTO orderr (date_placed, account_id, status, paid, margin_percentage, carport_length_cm, carport_width_cm, carport_height_cm, shed_length_cm, shed_width_cm) VALUES
                            (CURRENT_DATE, 1, 'henvendelse', FALSE, 30, 780, 600, 230, 210, 210),
                            (CURRENT_DATE - INTERVAL '1 day', 1, 'betalt', TRUE, 30, 580, 530, 230, 150, 210)
                    """);

            stmt.execute("""
                        INSERT INTO orderline (item_id, quantity, orderr_id, cost_price) VALUES
                            (1, 8, 1, 120),
                            (2, 15, 1, 75),
                            (1, 6, 2, 120),
                            (2, 12, 2, 75)
                    """);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}