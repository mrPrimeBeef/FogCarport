-- Drop all tables in the public schema if they exist and drop the schema
DO $$
DECLARE
table_name text;
BEGIN
    -- Loop through all tables in the public schema
FOR table_name IN
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
    LOOP
        EXECUTE format('DROP TABLE IF EXISTS public.%I CASCADE;', table_name);
END LOOP;
END $$;

-- Drop the public schema if it exists
DROP SCHEMA IF EXISTS public CASCADE;

-- Recreate the public schema to ensure a clean state
CREATE SCHEMA public;

-- Grant default privileges on the public schema
GRANT ALL ON SCHEMA public TO public;
GRANT ALL ON SCHEMA public TO postgres;

-- Drop the test schema if it exists
DROP SCHEMA IF EXISTS test CASCADE;

-- Recreate the test schema
CREATE SCHEMA test;

-- Grant default privileges on the test schema
GRANT ALL ON SCHEMA test TO public;
GRANT ALL ON SCHEMA test TO postgres;

-- Begin creating tables in the public schema
BEGIN;

CREATE TABLE IF NOT EXISTS public.account
(
    account_id serial NOT NULL,
    email character varying(64) NOT NULL,
    password character varying(64) NOT NULL,
    name character varying,
    role character varying DEFAULT 'customer',
    address character varying,
    zip_code integer,
    PRIMARY KEY (account_id),
    CONSTRAINT email_unique UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS public.orderr
(
    orderr_id serial NOT NULL,
    date_placed date,
    date_completed date,
    account_id integer NOT NULL,
    status character varying NOT NULL,
    paid boolean DEFAULT false,
    date_paid date,
    sale_price numeric,
    carport_length_cm integer NOT NULL,
    carport_width_cm integer NOT NULL,
    carport_height_cm integer,
    shed_length_cm integer,
    shed_width_cm integer,
    CONSTRAINT orders_pkey PRIMARY KEY (orderr_id)
    );

CREATE TABLE IF NOT EXISTS public.item
(
    item_id serial NOT NULL,
    description character varying NOT NULL,
    length_cm numeric,
    width_cm numeric,
    height_cm numeric,
    cost_price numeric NOT NULL,
    PRIMARY KEY (item_id)
    );

CREATE TABLE IF NOT EXISTS public.orderline
(
    orderline_id serial NOT NULL,
    item_id integer NOT NULL,
    quantity integer NOT NULL,
    orderr_id integer NOT NULL,
    cost_price numeric NOT NULL,
    PRIMARY KEY (orderline_id)
    );

CREATE TABLE IF NOT EXISTS public.zip_code
(
    zip_code integer NOT NULL,
    city character varying,
    PRIMARY KEY (zip_code)
    );

ALTER TABLE IF EXISTS public.account
    ADD CONSTRAINT fk_zip FOREIGN KEY (zip_code)
    REFERENCES public.zip_code (zip_code) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.orderr
    ADD CONSTRAINT fk FOREIGN KEY (account_id)
    REFERENCES public.account (account_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.orderline
    ADD CONSTRAINT fk_orderr_id FOREIGN KEY (orderr_id)
    REFERENCES public.orderr (orderr_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.orderline
    ADD CONSTRAINT fk_item_id FOREIGN KEY (item_id)
    REFERENCES public.item (item_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;

END;

-- Begin creating tables in the test schema
BEGIN;

CREATE TABLE IF NOT EXISTS test.account
(
    account_id serial NOT NULL,
    email character varying(64) NOT NULL,
    password character varying(64) NOT NULL,
    name character varying,
    role character varying DEFAULT 'customer',
    address character varying,
    zip_code integer,
    PRIMARY KEY (account_id),
    CONSTRAINT email_unique_test UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS test.orderr
(
    orderr_id serial NOT NULL,
    date_placed date,
    date_completed date,
    account_id integer NOT NULL,
    status character varying NOT NULL,
    paid boolean DEFAULT false,
    date_paid date,
    sale_price numeric,
    carport_length_cm integer NOT NULL,
    carport_width_cm integer NOT NULL,
    carport_height_cm integer,
    shed_length_cm integer,
    shed_width_cm integer,
    CONSTRAINT orders_pkey_test PRIMARY KEY (orderr_id)
    );

CREATE TABLE IF NOT EXISTS test.item
(
    item_id serial NOT NULL,
    description character varying NOT NULL,
    length_cm numeric,
    width_cm numeric,
    height_cm numeric,
    cost_price numeric NOT NULL,
    PRIMARY KEY (item_id)
    );

CREATE TABLE IF NOT EXISTS test.orderline
(
    orderline_id serial NOT NULL,
    item_id integer NOT NULL,
    quantity integer NOT NULL,
    orderr_id integer NOT NULL,
    cost_price numeric NOT NULL,
    PRIMARY KEY (orderline_id)
    );

CREATE TABLE IF NOT EXISTS test.zip_code
(
    zip_code integer NOT NULL,
    city character varying,
    PRIMARY KEY (zip_code)
    );

ALTER TABLE IF EXISTS test.account
    ADD CONSTRAINT fk_zip_test FOREIGN KEY (zip_code)
    REFERENCES test.zip_code (zip_code) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;

ALTER TABLE IF EXISTS test.orderr
    ADD CONSTRAINT fk_test FOREIGN KEY (account_id)
    REFERENCES test.account (account_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;

ALTER TABLE IF EXISTS test.orderline
    ADD CONSTRAINT fk_orderr_id_test FOREIGN KEY (orderr_id)
    REFERENCES test.orderr (orderr_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;

ALTER TABLE IF EXISTS test.orderline
    ADD CONSTRAINT fk_item_id_test FOREIGN KEY (item_id)
    REFERENCES test.item (item_id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION;

END;
