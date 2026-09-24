-- V2__seed.sql
-- Only structural/reference data (no plain-text passwords).
-- Actual user + book seed data is inserted by DataSeeder (CommandLineRunner)
-- which hashes passwords via BCrypt at application startup.
-- This file intentionally left minimal to comply with the rule:
--   "hash passwords through the app, never hardcoded plain text".

-- No DML here – DataSeeder handles it.
SELECT 1;
