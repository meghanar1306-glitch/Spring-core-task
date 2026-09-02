-- Employee Leave Management Portal - Database Setup Script
-- Run this once against your MySQL server before starting the application.
-- (Hibernate is also configured with hibernate.hbm2ddl.auto=update, so tables
--  will be created automatically on first run - this script is provided so
--  you can set up the database and sample/test data explicitly, and it also
--  documents the schema clearly for the evaluator.)

CREATE DATABASE IF NOT EXISTS leave_portal_db;
USE leave_portal_db;

-- ============================================================
-- Table: employee
-- ============================================================
CREATE TABLE IF NOT EXISTS employee (
    employee_id   VARCHAR(20)  NOT NULL,
    password      VARCHAR(100) NOT NULL,
    name          VARCHAR(100) NOT NULL,
    department    VARCHAR(100) NOT NULL,
    leave_balance INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (employee_id)
);

-- ============================================================
-- Table: leave_request
-- ============================================================
CREATE TABLE IF NOT EXISTS leave_request (
    request_id     BIGINT AUTO_INCREMENT,
    employee_id    VARCHAR(20)  NOT NULL,
    leave_type     VARCHAR(20)  NOT NULL,
    from_date      DATE         NOT NULL,
    to_date        DATE         NOT NULL,
    number_of_days INT          NOT NULL,
    reason         VARCHAR(500) NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    created_date   DATETIME     NOT NULL,
    PRIMARY KEY (request_id),
    CONSTRAINT fk_leave_request_employee
        FOREIGN KEY (employee_id) REFERENCES employee (employee_id)
);

-- ============================================================
-- Test / demo data
-- Test credentials for the evaluator: Employee ID = EMP001, Password = password123
-- ============================================================
INSERT INTO employee (employee_id, password, name, department, leave_balance) VALUES
    ('EMP001', 'password123', ' Anusha',    'Engineering', 18),
    ('EMP002', 'password123', 'Dhanush', 'Human Resources', 20),
    ('EMP003', 'password123', 'Divya', 'Marketing', 10),
    ('EMP004', 'password123', 'Priya', 'Finance', 12),
    ('EMP003', 'password123', 'Nandini', 'Development', 14)
ON DUPLICATE KEY UPDATE employee_id = employee_id;

INSERT INTO leave_request (employee_id, leave_type, from_date, to_date, number_of_days, reason, status, created_date) VALUES
    ('EMP001', 'CASUAL', '2026-06-10', '2026-06-11', 2, 'Family function', 'APPROVED', '2026-06-01 09:15:00'),
    ('EMP001', 'SICK',   '2026-07-02', '2026-07-02', 1, 'Fever',            'REJECTED', '2026-07-01 08:40:00');
