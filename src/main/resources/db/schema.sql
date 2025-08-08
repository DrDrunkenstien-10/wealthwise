CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- SCHEMA: account

-- DROP SCHEMA IF EXISTS account ;

CREATE SCHEMA IF NOT EXISTS account
    AUTHORIZATION postgres;


-- Table: account.users

-- DROP TABLE IF EXISTS account.users;

CREATE TABLE IF NOT EXISTS account.users
(
    user_id uuid NOT NULL DEFAULT uuid_generate_v4(),
    username character varying(50) COLLATE pg_catalog."default" NOT NULL,
    email character varying(100) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    keycloak_id text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT users_email_key UNIQUE (email),
    CONSTRAINT users_username_key UNIQUE (username)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS account.users
    OWNER to postgres;


-- SCHEMA: finance

-- DROP SCHEMA IF EXISTS finance ;

CREATE SCHEMA IF NOT EXISTS finance
    AUTHORIZATION postgres;


-- Table: finance.transactions

-- DROP TABLE IF EXISTS finance.transactions;

CREATE TABLE IF NOT EXISTS finance.transactions
(
    transaction_id uuid NOT NULL DEFAULT uuid_generate_v4(),
    user_id uuid NOT NULL,
    name character varying(100) COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default",
    amount numeric(12,2) NOT NULL,
    category character varying(50) COLLATE pg_catalog."default",
    payment_type character varying(50) COLLATE pg_catalog."default",
    date timestamp without time zone NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    transaction_type character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT transactions_pkey PRIMARY KEY (transaction_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES account.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT transactions_amount_check CHECK (amount >= 0::numeric)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS finance.transactions
    OWNER to postgres;
-- Index: unique_transaction_name_per_user_idx

-- DROP INDEX IF EXISTS finance.unique_transaction_name_per_user_idx;

CREATE UNIQUE INDEX IF NOT EXISTS unique_transaction_name_per_user_idx
    ON finance.transactions USING btree
    (user_id ASC NULLS LAST, lower(name::text) COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;


-- Table: finance.receipt

-- DROP TABLE IF EXISTS finance.receipt;

CREATE TABLE IF NOT EXISTS finance.receipt
(
    receipt_id uuid NOT NULL DEFAULT uuid_generate_v4(),
    user_id uuid NOT NULL,
    transaction_id uuid NOT NULL,
    file_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    file_path character varying(1024) COLLATE pg_catalog."default",
    upload_timestamp timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    CONSTRAINT receipt_pkey PRIMARY KEY (receipt_id),
    CONSTRAINT fk_receipt_transaction FOREIGN KEY (transaction_id)
        REFERENCES finance.transactions (transaction_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_receipt_user FOREIGN KEY (user_id)
        REFERENCES account.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS finance.receipt
    OWNER to postgres;


-- Table: finance.recurring_transactions

-- DROP TABLE IF EXISTS finance.recurring_transactions;

CREATE TABLE IF NOT EXISTS finance.recurring_transactions
(
    recurring_transaction_id uuid NOT NULL DEFAULT uuid_generate_v4(),
    user_id uuid NOT NULL,
    recurring_transaction_name text COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default",
    amount numeric(12,2) NOT NULL,
    category text COLLATE pg_catalog."default" NOT NULL,
    payment_type text COLLATE pg_catalog."default" NOT NULL,
    transaction_type text COLLATE pg_catalog."default" NOT NULL,
    frequency text COLLATE pg_catalog."default" NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    next_occurrence date,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone,
    updated_at timestamp without time zone,
    CONSTRAINT recurring_transactions_pkey PRIMARY KEY (recurring_transaction_id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES account.users (user_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS finance.recurring_transactions
    OWNER to postgres;
-- Index: unique_recurring_transaction_name_per_user_idx

-- DROP INDEX IF EXISTS finance.unique_recurring_transaction_name_per_user_idx;

CREATE UNIQUE INDEX IF NOT EXISTS unique_recurring_transaction_name_per_user_idx
    ON finance.recurring_transactions USING btree
    (user_id ASC NULLS LAST, lower(recurring_transaction_name) COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;