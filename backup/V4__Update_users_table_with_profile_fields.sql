-- Add soft delete column to users table
ALTER TABLE users
    ADD (deleted NUMBER(1) DEFAULT 0);
