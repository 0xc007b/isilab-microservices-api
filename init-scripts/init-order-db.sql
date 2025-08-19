-- Script d'initialisation pour la base commande
CREATE SCHEMA IF NOT EXISTS order_schema;

-- Création d'un utilisateur de lecture pour monitoring
CREATE USER order_reader WITH PASSWORD 'read_only_pass';
GRANT CONNECT ON DATABASE order_db TO order_reader;
GRANT USAGE ON SCHEMA public TO order_reader;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO order_reader;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO order_reader;

-- Extensions utiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
