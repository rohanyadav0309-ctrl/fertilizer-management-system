-- =======================================================
-- Database: fertilizer_management
-- Project: Fertilizer Management System (Java Swing + JDBC)
-- Suitable for: 2nd Year Engineering College Practical Project
-- =======================================================

-- Step 1: Create Database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS fertilizer_management;
USE fertilizer_management;

-- Step 2: Drop existing tables (in reverse foreign-key order) to prevent conflicts
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS fertilizer;
DROP TABLE IF EXISTS admin;

-- =======================================================
-- Table 1: admin
-- Stores administrator login credentials
-- =======================================================
CREATE TABLE admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

-- Sample Admin Credentials (username: admin, password: admin123)
INSERT INTO admin (username, password, full_name) VALUES
('admin', 'admin123', 'Store Administrator'),
('staff', 'staff123', 'Inventory Manager');


-- =======================================================
-- Table 2: fertilizer
-- Stores fertilizer inventory details
-- =======================================================
CREATE TABLE fertilizer (
    fertilizer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL
);

-- Sample Fertilizer Records
INSERT INTO fertilizer (name, type, price, quantity) VALUES
('Neem Coated Urea (45kg)', 'Nitrogenous', 266.50, 150),
('DAP - Di-Ammonium Phosphate (50kg)', 'Phosphatic', 1350.00, 80),
('MOP - Muriate of Potash (50kg)', 'Potassic', 1700.00, 45),
('NPK 19:19:19 Complex (50kg)', 'Complex', 1450.00, 60),
('Single Super Phosphate - SSP (50kg)', 'Phosphatic', 550.00, 30),
('Zinc Sulphate Micronutrient (10kg)', 'Micronutrient', 420.00, 8), -- Low stock for demonstration
('Organic Vermicompost (40kg)', 'Organic', 350.00, 5),   -- Low stock for demonstration
('Bio-Potash Liquid (1 Litre)', 'Bio-Fertilizer', 480.00, 25);


-- =======================================================
-- Table 3: customer
-- Stores customer (farmer / buyer) contact details
-- =======================================================
CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(15) NOT NULL,
    address VARCHAR(200) NOT NULL
);

-- Sample Customer Records
INSERT INTO customer (customer_name, mobile_number, address) VALUES
('Ramesh Kumar', '9876543210', 'Village Ramgarh, Post Khair'),
('Suresh Patel', '9823456789', 'Plot 45, Green Valley Farm'),
('Anita Sharma', '9712345678', 'House 12, Krishi Mandi Road'),
('Rajesh Yadav', '9934567812', 'Farmhouse 7, Canal Colony'),
('Vikram Singh', '9412309876', 'Sector 3, Agro Hub');


-- =======================================================
-- Table 4: sales
-- Stores sales transactions and billing records
-- =======================================================
CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    fertilizer_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sales_fertilizer FOREIGN KEY (fertilizer_id) 
        REFERENCES fertilizer(fertilizer_id) 
        ON DELETE CASCADE
);

-- Sample Sales Records
INSERT INTO sales (fertilizer_id, customer_name, quantity, total_price, sale_date) VALUES
(1, 'Ramesh Kumar', 10, 2665.00, '2026-10-25 10:30:00'),
(2, 'Suresh Patel', 4, 5400.00, '2026-10-26 14:15:00'),
(3, 'Anita Sharma', 2, 3400.00, '2026-10-28 11:00:00'),
(4, 'Rajesh Yadav', 5, 7250.00, '2026-10-30 09:45:00');
