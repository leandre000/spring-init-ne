-- =============================================================================
-- V3 — Seed Data (Roles, Users, and Tariffs)
-- =============================================================================

-- Seed Roles
INSERT INTO roles (id, name) VALUES 
(1, 'ROLE_ADMIN'),
(2, 'ROLE_OPERATOR'),
(3, 'ROLE_FINANCE'),
(4, 'ROLE_CUSTOMER');

-- Reset BIGSERIAL sequence for roles table
ALTER SEQUENCE roles_id_seq RESTART WITH 5;

-- Seed Users (Password for all accounts is 'Secret@123' hashed with BCrypt)
-- Hash: $2a$10$W4XLjXRhHBtjO/9AwezBwuiLC7GJ1BxEsfeFRP5.cQOP8OcUJNurm
INSERT INTO users (id, full_name, email, phone_number, password, status, role_id, created_by) VALUES
(1, 'System Administrator', 'admin@utility.com', '+250780000001', '$2a$10$W4XLjXRhHBtjO/9AwezBwuiLC7GJ1BxEsfeFRP5.cQOP8OcUJNurm', 'ACTIVE', 1, 'system'),
(2, 'Utility Operator', 'operator@utility.com', '+250780000002', '$2a$10$W4XLjXRhHBtjO/9AwezBwuiLC7GJ1BxEsfeFRP5.cQOP8OcUJNurm', 'ACTIVE', 2, 'system'),
(3, 'Finance Officer', 'finance@utility.com', '+250780000003', '$2a$10$W4XLjXRhHBtjO/9AwezBwuiLC7GJ1BxEsfeFRP5.cQOP8OcUJNurm', 'ACTIVE', 3, 'system'),
(4, 'Customer Support User', 'customer@utility.com', '+250780000004', '$2a$10$W4XLjXRhHBtjO/9AwezBwuiLC7GJ1BxEsfeFRP5.cQOP8OcUJNurm', 'ACTIVE', 4, 'system');

-- Reset BIGSERIAL sequence for users table
ALTER SEQUENCE users_id_seq RESTART WITH 5;

-- Seed Standard Tariffs
INSERT INTO tariffs (id, tariff_name, meter_type, tariff_type, rate_per_unit, fixed_charge, vat_percentage, penalty_percentage, version, effective_from, status, created_by) VALUES
(1, 'Water Standard Flat', 'WATER', 'FLAT', 320.00, 1000.00, 18.00, 5.00, 1, '2026-01-01', 'ACTIVE', 'system'),
(2, 'Electricity Standard Flat', 'ELECTRICITY', 'FLAT', 134.50, 1500.00, 18.00, 5.00, 1, '2026-01-01', 'ACTIVE', 'system');

-- Reset BIGSERIAL sequence for tariffs table
ALTER SEQUENCE tariffs_id_seq RESTART WITH 3;
