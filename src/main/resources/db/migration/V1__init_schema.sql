-- =============================================================================
-- V1 — Utility Billing System Initial Schema
-- =============================================================================

-- Create roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(254) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    role_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Create customers table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    national_id VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(254) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    registration_date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100)
);

-- Create meters table
CREATE TABLE meters (
    id BIGSERIAL PRIMARY KEY,
    meter_number VARCHAR(50) UNIQUE NOT NULL,
    meter_type VARCHAR(20) NOT NULL, -- WATER, ELECTRICITY
    installation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    customer_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100),
    CONSTRAINT fk_meters_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

-- Create meter_readings table
CREATE TABLE meter_readings (
    id BIGSERIAL PRIMARY KEY,
    meter_id BIGINT NOT NULL,
    previous_reading DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    current_reading DECIMAL(12, 2) NOT NULL,
    consumption DECIMAL(12, 2) NOT NULL,
    reading_date DATE NOT NULL DEFAULT CURRENT_DATE,
    month INT NOT NULL,
    year INT NOT NULL,
    captured_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100),
    CONSTRAINT fk_readings_meter FOREIGN KEY (meter_id) REFERENCES meters (id),
    CONSTRAINT fk_readings_user FOREIGN KEY (captured_by) REFERENCES users (id),
    CONSTRAINT uq_meter_month_year UNIQUE (meter_id, month, year)
);

-- Create tariffs table
CREATE TABLE tariffs (
    id BIGSERIAL PRIMARY KEY,
    tariff_name VARCHAR(100) NOT NULL,
    meter_type VARCHAR(20) NOT NULL, -- WATER, ELECTRICITY
    tariff_type VARCHAR(20) NOT NULL, -- FLAT, TIERED
    rate_per_unit DECIMAL(10, 4) NOT NULL,
    fixed_charge DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    vat_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    penalty_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    version INT NOT NULL DEFAULT 1,
    effective_from DATE NOT NULL,
    effective_to DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100)
);

-- Create bills table
CREATE TABLE bills (
    id BIGSERIAL PRIMARY KEY,
    bill_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    meter_id BIGINT NOT NULL,
    meter_reading_id BIGINT NOT NULL,
    tariff_id BIGINT NOT NULL,
    billing_month INT NOT NULL,
    billing_year INT NOT NULL,
    consumption DECIMAL(12, 2) NOT NULL,
    amount_before_tax DECIMAL(12, 2) NOT NULL,
    tax_amount DECIMAL(12, 2) NOT NULL,
    penalty_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12, 2) NOT NULL,
    paid_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    balance DECIMAL(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PARTIAL, PAID, OVERDUE
    generated_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100),
    CONSTRAINT fk_bills_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_bills_meter FOREIGN KEY (meter_id) REFERENCES meters (id),
    CONSTRAINT fk_bills_reading FOREIGN KEY (meter_reading_id) REFERENCES meter_readings (id),
    CONSTRAINT fk_bills_tariff FOREIGN KEY (tariff_id) REFERENCES tariffs (id)
);

-- Create payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    payment_reference VARCHAR(50) UNIQUE NOT NULL,
    bill_id BIGINT NOT NULL,
    amount_paid DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL, -- CASH, MOBILE_MONEY, BANK, CARD
    payment_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    received_by BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100),
    CONSTRAINT fk_payments_bill FOREIGN KEY (bill_id) REFERENCES bills (id),
    CONSTRAINT fk_payments_user FOREIGN KEY (received_by) REFERENCES users (id)
);

-- Create notifications table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_notifications_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_customers_code ON customers(customer_code);
CREATE INDEX idx_meters_number ON meters(meter_number);
CREATE INDEX idx_readings_meter_date ON meter_readings(meter_id, month, year);
CREATE INDEX idx_bills_number ON bills(bill_number);
CREATE INDEX idx_payments_ref ON payments(payment_reference);
CREATE INDEX idx_notifications_customer ON notifications(customer_id);
