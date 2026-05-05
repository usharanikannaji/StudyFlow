-- ============================================
--   STUDYFLOW — schema.sql
--   MySQL Database Schema
--   Developer: Usha Rani Kannaji
-- ============================================

-- Create and use database
CREATE DATABASE IF NOT EXISTS studyflow_db;
USE studyflow_db;

-- Drop if exists (for fresh setup)
DROP TABLE IF EXISTS tasks;

-- Create tasks table
CREATE TABLE tasks (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    title      VARCHAR(255)                      NOT NULL,
    subject    VARCHAR(100)                      NOT NULL,
    priority   ENUM('High', 'Medium', 'Low')     NOT NULL,
    deadline   DATE                              NOT NULL,
    notes      TEXT,
    status     ENUM('Pending', 'Completed')      DEFAULT 'Pending',
    created_at TIMESTAMP                         DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Sample data
INSERT INTO tasks (title, subject, priority, deadline, notes, status) VALUES
('Complete DSA Chapter 5 — Trees & Graphs', 'Data Structures',    'High',   DATE_ADD(CURDATE(), INTERVAL 3  DAY), 'Focus on BFS, DFS and binary trees',           'Pending'),
('Practice Flask REST API endpoints',        'Backend Dev',        'High',   DATE_ADD(CURDATE(), INTERVAL 2  DAY), 'Cover GET, POST, PUT, DELETE methods',          'Pending'),
('Review Docker & Kubernetes basics',        'DevOps',             'Medium', DATE_ADD(CURDATE(), INTERVAL 5  DAY), 'Focus on containers, pods and deployments',     'Completed'),
('Solve 10 LeetCode problems',               'Problem Solving',    'High',   DATE_ADD(CURDATE(), INTERVAL 1  DAY), 'Arrays and linked lists section',               'Pending'),
('Build React component library',            'Frontend Dev',       'Medium', DATE_ADD(CURDATE(), INTERVAL 7  DAY), 'Create reusable UI components with props',      'Pending'),
('Study MySQL joins and subqueries',         'Database',           'Low',    DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'Inner, left, right, full outer joins',          'Pending');

-- Verify
SELECT id, title, subject, priority, status FROM tasks;
