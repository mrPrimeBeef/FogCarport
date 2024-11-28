package app.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import app.exceptions.AccountCreationException;
import app.exceptions.DatabaseException;
import app.entities.Account;

class AccountMapperTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "fog";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    public static void setUpClass() {
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {

            // Drop alle tabeller og sekvenser
            stmt.execute("DROP TABLE IF EXISTS orderline CASCADE");
            stmt.execute("DROP TABLE IF EXISTS orderr CASCADE");
            stmt.execute("DROP TABLE IF EXISTS account CASCADE");
            stmt.execute("DROP TABLE IF EXISTS item CASCADE");
            stmt.execute("DROP TABLE IF EXISTS zip_code CASCADE");

            stmt.execute("DROP SEQUENCE IF EXISTS account_account_id_seq CASCADE");
            stmt.execute("DROP SEQUENCE IF EXISTS item_item_id_seq CASCADE");
            stmt.execute("DROP SEQUENCE IF EXISTS orderline_orderline_id_seq CASCADE");
            stmt.execute("DROP SEQUENCE IF EXISTS orderr_orderr_id_seq CASCADE");

            // Opret tabeller
            stmt.execute("""
                        CREATE TABLE zip_code (
                            zip_code integer NOT NULL,
                            city character varying COLLATE pg_catalog."default",
                            CONSTRAINT zip_code_pkey PRIMARY KEY (zip_code)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE account (
                            account_id serial NOT NULL,
                            email character varying(64) COLLATE pg_catalog."default" NOT NULL,
                            password character varying(64) COLLATE pg_catalog."default" NOT NULL,
                            name character varying COLLATE pg_catalog."default",
                            role character varying COLLATE pg_catalog."default" DEFAULT 'customer'::character varying,
                            address character varying COLLATE pg_catalog."default",
                            zip_code integer,
                            phone character varying COLLATE pg_catalog."default" NOT NULL,
                            CONSTRAINT account_pkey PRIMARY KEY (account_id),
                            CONSTRAINT email_unique UNIQUE (email)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE item (
                            item_id serial NOT NULL,
                            description character varying COLLATE pg_catalog."default" NOT NULL,
                            length_cm numeric,
                            width_cm numeric,
                            height_cm numeric,
                            cost_price numeric NOT NULL,
                            CONSTRAINT item_pkey PRIMARY KEY (item_id)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE orderr (
                            orderr_id serial NOT NULL,
                            date_placed date,
                            date_completed date,
                            account_id integer NOT NULL,
                            status character varying COLLATE pg_catalog."default" NOT NULL,
                            paid boolean DEFAULT false,
                            date_paid date,
                            sale_price numeric,
                            carport_length_cm integer NOT NULL,
                            carport_width_cm integer NOT NULL,
                            carport_height_cm integer,
                            shed_length_cm integer,
                            shed_width_cm integer,
                            CONSTRAINT orders_pkey PRIMARY KEY (orderr_id)
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE orderline (
                            orderline_id serial NOT NULL,
                            item_id integer NOT NULL,
                            quantity integer NOT NULL,
                            orderr_id integer NOT NULL,
                            cost_price numeric NOT NULL,
                            CONSTRAINT orderline_pkey PRIMARY KEY (orderline_id)
                        )
                    """);

            // Tilføj foreign keys
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

            // Indsæt test data
            stmt.execute("""
                        INSERT INTO zip_code (zip_code, city) VALUES 
                            (2100, 'København Ø'),
                            (2200, 'København N'),
                            (2300, 'København S'),
                            (2400, 'København NV')
                    """);

            stmt.execute("""
                        INSERT INTO account (email, password, name, role, address, zip_code, phone) VALUES
                            ('test@test.dk', '1234', 'Test Testersen', 'customer', 'Testvej 1', 2100, '12345678'),
                            ('admin@admin.dk', 'admin', 'Admin Admin', 'admin', 'Adminvej 1', 2200, '87654321'),
                            ('kunde@kunde.dk', 'kunde', 'Kurt Kunde', 'customer', 'Kundevej 123', 2300, '11223344')
                    """);

            stmt.execute("""
                        INSERT INTO item (description, length_cm, width_cm, height_cm, cost_price) VALUES
                            ('97x97 mm. trykimp. Stolpe', 300, 9.7, 9.7, 120),
                            ('45x195 mm. spærtræ ubh.', 600, 4.5, 19.5, 75),
                            ('45x95 mm. Reglar ub.', 240, 4.5, 9.5, 40),
                            ('19x100 mm. trykimp. Brædt', 210, 1.9, 10, 25),
                            ('25x150 mm. trykimp. Brædt', 400, 2.5, 15, 45)
                    """);

            stmt.execute("""
                        INSERT INTO orderr (date_placed, account_id, status, paid, carport_length_cm, carport_width_cm, carport_height_cm, shed_length_cm, shed_width_cm) VALUES
                            (CURRENT_DATE, 1, 'PENDING', false, 780, 600, 230, 210, 210),
                            (CURRENT_DATE - INTERVAL '1 day', 1, 'CONFIRMED', true, 580, 530, 230, 150, 210),
                            (CURRENT_DATE - INTERVAL '5 days', 3, 'COMPLETED', true, 780, 600, 230, 210, 210)
                    """);

            stmt.execute("""
                        INSERT INTO orderline (item_id, quantity, orderr_id, cost_price) VALUES 
                            (1, 8, 1, 120),
                            (2, 15, 1, 75),
                            (3, 4, 1, 40),
                            (1, 6, 2, 120),
                            (2, 12, 2, 75),
                            (1, 8, 3, 120),
                            (2, 15, 3, 75),
                            (3, 4, 3, 40),
                            (4, 20, 3, 25),
                            (5, 15, 3, 45)
                    """);

            // Opdater priser på completed orders
            stmt.execute("""
                        UPDATE orderr 
                        SET sale_price = 15000, date_paid = CURRENT_DATE - INTERVAL '1 day'
                        WHERE orderr_id = 2
                    """);

            stmt.execute("""
                        UPDATE orderr 
                        SET sale_price = 25000, 
                            date_paid = CURRENT_DATE - INTERVAL '3 days',
                            date_completed = CURRENT_DATE - INTERVAL '2 days'
                        WHERE orderr_id = 3
                    """);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllEmailsFromAccount() throws DatabaseException {
        ArrayList<String> emails = new ArrayList<>();

        emails = AccountMapper.getAllAccountEmails(connectionPool);
        assertEquals(3, emails.size());

        String mail = emails.get(0);
        assertEquals("test@test.dk", mail);


        mail = emails.get(1);
        assertNotEquals("test@test.dk", mail);
    }

    @Test
    void getIdFromAccountEmail() throws AccountCreationException {
        int actual = AccountMapper.getAccountIdFromEmail("test@test.dk", connectionPool);
        assertEquals(1, actual);


        actual = AccountMapper.getAccountIdFromEmail("test@test.dk", connectionPool);
        assertNotEquals(0,actual);
    }

    @Test
    void createAccount() throws AccountCreationException {
        int actual = AccountMapper.createAccount("String name", "String adress", 2100, "String phone", "String email", connectionPool);
        assertEquals(4, actual);


        actual = AccountMapper.createAccount("String name2", "String adress2", 2100, "String phone2", "String email2", connectionPool);
        assertNotEquals(4, actual);
    }

    @Test
    void getAllAccounts() throws DatabaseException {
        ArrayList<Account> accounts = AccountMapper.getAllAccounts(connectionPool);
        String actual = accounts.get(0).getName();

        assertEquals("Test Testersen", actual);
        assertEquals(3, accounts.size());

        assertNotEquals("Test Testersen", accounts.get(1).getName());
    }
}