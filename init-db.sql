-- Initialize URL Shortener Database
-- This script runs when PostgreSQL container starts for the first time

-- Create database if not exists (this is handled by POSTGRES_DB env var)
-- But we can create additional databases or schemas here if needed

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create indexes for better performance (will be created by JPA but good to have them here too)
-- These will be created by Hibernate automatically, but we can add custom ones here

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE url_shortener TO urluser;

-- Create any additional users or roles if needed
-- CREATE ROLE read_only_user;
-- GRANT CONNECT ON DATABASE url_shortener TO read_only_user;
-- GRANT USAGE ON SCHEMA public TO read_only_user;
-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO read_only_user;

-- Insert any initial data if needed
-- This is optional and can be used for seeding initial data
