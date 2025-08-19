-- Script d'initialisation pour la base produit
CREATE SCHEMA IF NOT EXISTS product_schema;

-- Création d'un utilisateur de lecture pour monitoring
CREATE USER product_reader WITH PASSWORD 'read_only_pass';
GRANT CONNECT ON DATABASE product_db TO product_reader;
GRANT USAGE ON SCHEMA public TO product_reader;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO product_reader;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO product_reader;

-- Extensions utiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
