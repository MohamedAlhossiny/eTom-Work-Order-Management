-- Create database
CREATE DATABASE workorder_db;
USE workorder_db;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS cancel_work_order;
DROP TABLE IF EXISTS work_order_item;
DROP TABLE IF EXISTS place_ref;
DROP TABLE IF EXISTS related_party;
DROP TABLE IF EXISTS work_order;

-- Create work_order table
CREATE TABLE work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_id VARCHAR(255) NOT NULL,
    state VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create work_order_item table
CREATE TABLE work_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    action VARCHAR(255) NOT NULL,
    description TEXT,
    state VARCHAR(50) NOT NULL,
    sequence INT NOT NULL,
    FOREIGN KEY (work_order_id) REFERENCES work_order(id)
);

-- Create place_ref table with optional fields
CREATE TABLE place_ref (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    FOREIGN KEY (work_order_id) REFERENCES work_order(id)
);

-- Create related_party table with optional fields
CREATE TABLE related_party (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    FOREIGN KEY (work_order_id) REFERENCES work_order(id)
);

-- Create cancel_work_order table
CREATE TABLE cancel_work_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    requested_by VARCHAR(255) NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    FOREIGN KEY (work_order_id) REFERENCES work_order(id)
);
