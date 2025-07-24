-- Create schemas
CREATE SCHEMA IF NOT EXISTS "account";
CREATE SCHEMA IF NOT EXISTS "finance";

-- Enable UUID generation extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create users table
CREATE TABLE IF NOT EXISTS "account"."users" (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create transactions table
CREATE TABLE IF NOT EXISTS "finance"."transactions" (
    transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount >= 0),
    category VARCHAR(50),
    payment_type VARCHAR(50),
    date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES "account"."users"(user_id)
        ON DELETE CASCADE
);

ALTER TABLE finance.transactions
ADD COLUMN transaction_type VARCHAR(20);

-- Later added the following:
CREATE UNIQUE INDEX unique_transaction_name_per_user_idx
ON finance.transactions (user_id, lower(name));

CREATE TABLE IF NOT EXISTS finance.receipt (
    receipt_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    transaction_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(1024),
    upload_timestamp TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_receipt_user
        FOREIGN KEY (user_id)
        REFERENCES account.users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_receipt_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES finance.transactions(transaction_id)
        ON DELETE CASCADE
);

-- Create the recurring_transactions table
CREATE TABLE IF NOT EXISTS finance.recurring_transactions (
    recurring_transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    recurring_transaction_name TEXT NOT NULL,
    description TEXT,
    amount NUMERIC(12,2) NOT NULL,
    category TEXT NOT NULL,
    payment_type TEXT NOT NULL,
    transaction_type TEXT NOT NULL,
    frequency TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    next_occurrence DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES account.users (user_id)
        ON DELETE CASCADE
);

-- Later added the following:
CREATE UNIQUE INDEX unique_recurring_transaction_name_per_user_idx
ON finance.recurring_transactions (user_id, lower(recurring_transaction_name));

INSERT INTO finance.transactions(
    user_id, name, description, amount, category, payment_type, date, created_at, updated_at, transaction_type)
VALUES 
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Salary', 'Monthly salary payment', 5000.00, 'Income', 'Bank Transfer', '2025-06-28 19:14:00', '2025-06-28 19:15:00', '2025-06-28 19:15:00', 'INCOME'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Grocery Shopping', 'Weekly groceries', 120.45, 'Food', 'Credit Card', '2025-06-27 17:23:00', '2025-06-27 17:24:00', '2025-06-27 17:24:00', 'EXPENSE'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Freelance Work', 'Website design project', 800.00, 'Side Hustle', 'PayPal', '2025-06-25 10:00:00', '2025-06-25 10:01:00', '2025-06-25 10:01:00', 'INCOME'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Electricity Bill', 'Monthly electricity bill', 75.32, 'Utilities', 'Bank Transfer', '2025-06-24 09:00:00', '2025-06-24 09:01:00', '2025-06-24 09:01:00', 'EXPENSE'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Restaurant', 'Dinner with friends', 65.00, 'Dining Out', 'Debit Card', '2025-06-23 20:30:00', '2025-06-23 20:31:00', '2025-06-23 20:31:00', 'EXPENSE'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Stock Dividend', 'Quarterly dividend received', 150.00, 'Investments', 'Bank Transfer', '2025-06-22 08:15:00', '2025-06-22 08:16:00', '2025-06-22 08:16:00', 'INCOME'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Internet Bill', 'Home internet service', 50.00, 'Utilities', 'Credit Card', '2025-06-21 15:45:00', '2025-06-21 15:46:00', '2025-06-21 15:46:00', 'EXPENSE'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Book Sale', 'Sold used books online', 45.00, 'Miscellaneous', 'PayPal', '2025-06-20 11:00:00', '2025-06-20 11:01:00', '2025-06-20 11:01:00', 'INCOME'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Coffee', 'Morning coffee', 4.50, 'Food', 'Cash', '2025-06-19 08:30:00', '2025-06-19 08:31:00', '2025-06-19 08:31:00', 'EXPENSE'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Gym Membership', 'Monthly gym fee', 35.00, 'Health', 'Debit Card', '2025-06-18 07:00:00', '2025-06-18 07:01:00', '2025-06-18 07:01:00', 'EXPENSE'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Car Repair', 'Brake pad replacement', 300.00, 'Transportation', 'Credit Card', '2025-06-17 13:45:00', '2025-06-17 13:46:00', '2025-06-17 13:46:00', 'EXPENSE'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Cashback Reward', 'Credit card cashback', 25.00, 'Rewards', 'Credit Card', '2025-06-16 14:20:00', '2025-06-16 14:21:00', '2025-06-16 14:21:00', 'INCOME'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Movie Night', 'Cinema tickets and snacks', 28.00, 'Entertainment', 'Debit Card', '2025-06-15 21:00:00', '2025-06-15 21:01:00', '2025-06-15 21:01:00', 'EXPENSE'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Tutoring', 'Private tutoring session income', 120.00, 'Side Hustle', 'Cash', '2025-06-14 18:00:00', '2025-06-14 18:01:00', '2025-06-14 18:01:00', 'INCOME'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Bus Pass', 'Monthly public transport pass', 60.00, 'Transportation', 'Debit Card', '2025-06-13 09:30:00', '2025-06-13 09:31:00', '2025-06-13 09:31:00', 'EXPENSE');


INSERT INTO finance.recurring_transactions(
    user_id, recurring_transaction_name, description, amount, category, payment_type, transaction_type, frequency, start_date, end_date, next_occurrence, is_active, created_at, updated_at)
VALUES 
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Monthly Rent', 'Apartment rent payment', 1200.00, 'Housing', 'Bank Transfer', 'EXPENSE', 'MONTHLY', '2024-01-01', '2026-01-01', '2025-07-01', TRUE, '2024-01-01 09:00:00', '2025-06-28 10:00:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Gym Subscription', 'Monthly fitness membership', 40.00, 'Health', 'Credit Card', 'EXPENSE', 'MONTHLY', '2025-01-01', '2025-12-31', '2025-07-01', TRUE, '2025-01-01 08:00:00', '2025-06-28 08:30:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Netflix Subscription', 'Streaming service', 15.99, 'Entertainment', 'Credit Card', 'EXPENSE', 'MONTHLY', '2024-06-01', '2026-06-01', '2025-07-01', TRUE, '2024-06-01 10:00:00', '2025-06-28 10:30:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Monthly Salary', 'Primary job salary', 5000.00, 'Income', 'Bank Transfer', 'INCOME', 'MONTHLY', '2024-01-01', '2026-01-01', '2025-07-01', TRUE, '2024-01-01 09:00:00', '2025-06-28 09:00:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Freelance Retainer', 'Ongoing freelance work', 800.00, 'Side Hustle', 'PayPal', 'INCOME', 'MONTHLY', '2024-12-01', '2025-12-01', '2025-07-01', TRUE, '2024-12-01 14:00:00', '2025-06-28 14:15:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Spotify', 'Music subscription', 9.99, 'Entertainment', 'Debit Card', 'EXPENSE', 'MONTHLY', '2024-07-01', '2026-07-01', '2025-07-01', TRUE, '2024-07-01 08:00:00', '2025-06-28 08:10:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Electricity Bill', 'Estimated recurring electricity cost', 70.00, 'Utilities', 'Bank Transfer', 'EXPENSE', 'MONTHLY', '2024-02-01', '2025-12-01', '2025-07-01', TRUE, '2024-02-01 09:00:00', '2025-06-28 09:30:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Daily Coffee Budget', 'Daily morning coffee', 4.50, 'Food', 'Cash', 'EXPENSE', 'DAILY', '2025-06-01', '2025-12-31', '2025-07-01', TRUE, '2025-06-01 08:00:00', '2025-06-28 08:10:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Weekly Tutoring', 'Recurring tutoring income', 100.00, 'Side Hustle', 'Cash', 'INCOME', 'WEEKLY', '2025-03-01', '2025-12-31', '2025-07-01', TRUE, '2025-03-01 16:00:00', '2025-06-28 16:15:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Weekly Groceries', 'Grocery shopping budget', 125.00, 'Food', 'Credit Card', 'EXPENSE', 'WEEKLY', '2025-04-01', '2025-12-31', '2025-07-01', TRUE, '2025-04-01 12:00:00', '2025-06-28 12:30:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Yearly Insurance', 'Car insurance annual premium', 850.00, 'Insurance', 'Bank Transfer', 'EXPENSE', 'YEARLY', '2023-07-01', '2026-07-01', '2025-07-01', TRUE, '2023-07-01 11:00:00', '2025-06-28 11:30:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Internet Bill', 'Monthly internet payment', 55.00, 'Utilities', 'Credit Card', 'EXPENSE', 'MONTHLY', '2024-05-01', '2026-05-01', '2025-07-01', TRUE, '2024-05-01 07:00:00', '2025-06-28 07:45:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Child Support', 'Monthly child support', 600.00, 'Family', 'Bank Transfer', 'EXPENSE', 'MONTHLY', '2023-01-01', '2025-12-31', '2025-07-01', TRUE, '2023-01-01 08:30:00', '2025-06-28 08:45:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Annual Bonus', 'Year-end bonus from employer', 1000.00, 'Income', 'Bank Transfer', 'INCOME', 'YEARLY', '2023-12-31', '2026-12-31', '2025-12-31', TRUE, '2023-12-31 17:00:00', '2025-06-28 17:00:00'),
('038af5b2-62bc-4d89-b176-9b9b09976b8b', 'Pet Insurance', 'Monthly pet insurance plan', 30.00, 'Insurance', 'Credit Card', 'EXPENSE', 'MONTHLY', '2025-01-15', '2026-01-15', '2025-07-15', TRUE, '2025-01-15 10:00:00', '2025-06-28 10:20:00');

-- SELECT * FROM account.users;

-- SELECT * FROM finance.transactions;

-- SELECT * FROM finance.receipt;

-- SELECT * FROM finance.recurring_transactions;