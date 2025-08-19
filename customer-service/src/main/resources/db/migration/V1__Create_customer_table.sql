-- Migration V1: Création de la table customers
-- Auteur: System
-- Date: Initial migration
-- Description: Création de la table principale pour stocker les informations des clients
-- Compatible: H2 et PostgreSQL

-- Création de la table customers
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index sur l'email pour optimiser les recherches
CREATE INDEX idx_customers_email ON customers(email);

-- Index sur le nom pour optimiser les recherches par nom
CREATE INDEX idx_customers_nom ON customers(nom);

-- Index sur la date de création pour les tris chronologiques
CREATE INDEX idx_customers_date_creation ON customers(date_creation);

-- Index composé pour les recherches dans l'adresse (pour la ville)
CREATE INDEX idx_customers_adresse ON customers(adresse);

-- Contrainte d'unicité sur l'email
ALTER TABLE customers ADD CONSTRAINT uk_customers_email UNIQUE (email);

-- Insertion de données de test pour le développement
INSERT INTO customers (nom, email, telephone, adresse) VALUES
('Jean Dupont', 'jean.dupont@email.com', '0123456789', '123 Rue de la Paix, Paris'),
('Marie Martin', 'marie.martin@email.com', '0987654321', '456 Avenue des Champs, Lyon'),
('Pierre Durand', 'pierre.durand@email.com', '0147258369', '789 Boulevard Saint-Germain, Marseille'),
('Sophie Lefebvre', 'sophie.lefebvre@email.com', '0369258147', '321 Rue de Rivoli, Toulouse'),
('Thomas Bernard', 'thomas.bernard@email.com', '0258147369', '654 Place Bellecour, Nice');
