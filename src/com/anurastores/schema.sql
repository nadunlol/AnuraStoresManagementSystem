-- ============================================================
-- ANURA STORES MANAGEMENT SYSTEM
-- Database Schema
-- Database: MySQL
-- ============================================================

-- ------------------------------------------------------------
-- 1. CREATE DATABASE
-- ------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS anura_stores;

USE anura_stores;


-- ------------------------------------------------------------
-- 2. SUPPLIER
-- Supplier Management
-- ------------------------------------------------------------

CREATE TABLE supplier (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE'
);


-- ------------------------------------------------------------
-- 3. PRODUCT
-- Product Management
-- ------------------------------------------------------------

CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(30) NOT NULL UNIQUE,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    selling_price DECIMAL(10,2) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT chk_product_selling_price
        CHECK (selling_price >= 0)
);


-- ------------------------------------------------------------
-- 4. PRODUCT_SUPPLIER
-- Supplier <-> Product Many-to-Many Relationship
-- Supplier-specific purchasing information
-- ------------------------------------------------------------

CREATE TABLE product_supplier (
    product_id INT NOT NULL,
    supplier_id INT NOT NULL,
    supplier_product_code VARCHAR(50),
    cost_price DECIMAL(10,2) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',

    PRIMARY KEY (product_id, supplier_id),

    CONSTRAINT fk_product_supplier_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_product_supplier_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES supplier(supplier_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_product_supplier_cost_price
        CHECK (cost_price >= 0)
);


-- ------------------------------------------------------------
-- 5. EMPLOYEE
-- Employee Management
-- ------------------------------------------------------------

CREATE TABLE employee (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    job_title VARCHAR(50) NOT NULL,
    hire_date DATE NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE'
);


-- ------------------------------------------------------------
-- 6. ATTENDANCE
-- Attendance Handling
-- ------------------------------------------------------------

CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in TIME,
    check_out TIME,
    status ENUM('PRESENT', 'ABSENT', 'LATE', 'LEAVE')
        NOT NULL DEFAULT 'PRESENT',
    remarks VARCHAR(255),

    CONSTRAINT fk_attendance_employee
        FOREIGN KEY (employee_id)
        REFERENCES employee(employee_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    -- One attendance record per employee per day
    CONSTRAINT uq_employee_attendance_date
        UNIQUE (employee_id, attendance_date)
);


-- ------------------------------------------------------------
-- 7. INVENTORY
-- Inventory Management
-- One inventory record per product
-- ------------------------------------------------------------

CREATE TABLE inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL UNIQUE,
    quantity INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 0,
    last_updated DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_inventory_quantity
        CHECK (quantity >= 0),

    CONSTRAINT chk_inventory_reorder_level
        CHECK (reorder_level >= 0)
);


-- ------------------------------------------------------------
-- 8. SALE
-- Sales Management
-- ------------------------------------------------------------

CREATE TABLE sale (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    employee_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'OTHER')
        NOT NULL DEFAULT 'CASH',

    CONSTRAINT fk_sale_employee
        FOREIGN KEY (employee_id)
        REFERENCES employee(employee_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_sale_total_amount
        CHECK (total_amount >= 0)
);


-- ------------------------------------------------------------
-- 9. SALE_ITEM
-- Products contained within each sale
-- ------------------------------------------------------------

CREATE TABLE sale_item (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_sale_item_sale
        FOREIGN KEY (sale_id)
        REFERENCES sale(sale_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_sale_item_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT chk_sale_item_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_sale_item_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_sale_item_subtotal
        CHECK (subtotal >= 0)
);


-- ------------------------------------------------------------
-- 10. INDEXES
-- Improve commonly used searches and joins
-- ------------------------------------------------------------

CREATE INDEX idx_product_name
    ON product(product_name);

CREATE INDEX idx_product_category
    ON product(category);

CREATE INDEX idx_supplier_name
    ON supplier(supplier_name);

CREATE INDEX idx_employee_name
    ON employee(last_name, first_name);

CREATE INDEX idx_attendance_date
    ON attendance(attendance_date);

CREATE INDEX idx_sale_date
    ON sale(sale_date);

CREATE INDEX idx_sale_item_product
    ON sale_item(product_id);

