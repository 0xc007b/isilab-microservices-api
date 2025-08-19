-- Script d'initialisation pour la base client
CREATE SCHEMA IF NOT EXISTS client_schema;

-- Création d'un utilisateur de lecture pour monitoring
CREATE USER client_reader WITH PASSWORD 'read_only_pass';
GRANT CONNECT ON DATABASE client_db TO client_reader;
GRANT USAGE ON SCHEMA public TO client_reader;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO client_reader;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO client_reader;

-- Extensions utiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
