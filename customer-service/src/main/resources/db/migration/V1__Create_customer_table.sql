-- Migration V1: Création de la table customers
-- Auteur: System
-- Date: Initial migration
-- Description: Création de la table principale pour stocker les informations des clients

-- Création de la table customers
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
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

-- Contrainte pour vérifier que l'email n'est pas vide
ALTER TABLE customers ADD CONSTRAINT chk_customers_email_not_empty
    CHECK (email IS NOT NULL AND TRIM(email) <> '');

-- Contrainte pour vérifier que le nom n'est pas vide
ALTER TABLE customers ADD CONSTRAINT chk_customers_nom_not_empty
    CHECK (nom IS NOT NULL AND TRIM(nom) <> '');

-- Contrainte pour vérifier la longueur minimale du nom
ALTER TABLE customers ADD CONSTRAINT chk_customers_nom_length
    CHECK (LENGTH(TRIM(nom)) >= 2);

-- Contrainte pour vérifier le format basique de l'email
ALTER TABLE customers ADD CONSTRAINT chk_customers_email_format
    CHECK (email ~* '^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\.[A-Za-z]{2,})$');

-- Fonction pour mettre à jour automatiquement date_modification
CREATE OR REPLACE FUNCTION update_date_modification()
RETURNS TRIGGER AS $$
BEGIN
    NEW.date_modification = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger pour mettre à jour automatiquement date_modification lors des UPDATE
CREATE TRIGGER trg_customers_update_date_modification
    BEFORE UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION update_date_modification();

-- Insertion de données de test (optionnel, pour le développement)
INSERT INTO customers (nom, email, telephone, adresse) VALUES
('Jean Dupont', 'jean.dupont@email.com', '0123456789', '123 Rue de la Paix, Paris'),
('Marie Martin', 'marie.martin@email.com', '0987654321', '456 Avenue des Champs, Lyon'),
('Pierre Durand', 'pierre.durand@email.com', '0147258369', '789 Boulevard Saint-Germain, Marseille'),
('Sophie Lefebvre', 'sophie.lefebvre@email.com', '0369258147', '321 Rue de Rivoli, Toulouse'),
('Thomas Bernard', 'thomas.bernard@email.com', '0258147369', '654 Place Bellecour, Nice');

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE customers IS 'Table principale pour stocker les informations des clients';
COMMENT ON COLUMN customers.id IS 'Identifiant unique auto-incrémenté du client';
COMMENT ON COLUMN customers.nom IS 'Nom complet du client (2-100 caractères)';
COMMENT ON COLUMN customers.email IS 'Adresse email unique du client';
COMMENT ON COLUMN customers.telephone IS 'Numéro de téléphone du client (optionnel, max 20 caractères)';
COMMENT ON COLUMN customers.adresse IS 'Adresse postale complète du client (optionnel, max 255 caractères)';
COMMENT ON COLUMN customers.date_creation IS 'Date et heure de création du client';
COMMENT ON COLUMN customers.date_modification IS 'Date et heure de dernière modification du client';
