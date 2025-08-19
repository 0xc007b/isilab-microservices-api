-- Migration V1: Création de la table des produits
-- Auteur: Product Service Team
-- Date: 2024-01-01
-- Description: Création de la table products avec toutes les contraintes et index nécessaires

-- Création de la table products
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    prix DECIMAL(10,2) NOT NULL,
    quantite_stock INTEGER NOT NULL DEFAULT 0,
    categorie VARCHAR(100) NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Contraintes
    CONSTRAINT chk_prix_positive CHECK (prix > 0),
    CONSTRAINT chk_stock_non_negatif CHECK (quantite_stock >= 0),
    CONSTRAINT chk_nom_non_vide CHECK (LENGTH(TRIM(nom)) > 0),
    CONSTRAINT chk_categorie_non_vide CHECK (LENGTH(TRIM(categorie)) > 0)
);

-- Index pour améliorer les performances des requêtes
CREATE INDEX idx_products_nom ON products(nom);
CREATE INDEX idx_products_categorie ON products(categorie);
CREATE INDEX idx_products_prix ON products(prix);
CREATE INDEX idx_products_stock ON products(quantite_stock);
CREATE INDEX idx_products_date_creation ON products(date_creation);

-- Index composé pour les recherches multicritères
CREATE INDEX idx_products_categorie_stock ON products(categorie, quantite_stock);
CREATE INDEX idx_products_prix_stock ON products(prix, quantite_stock);

-- Index unique sur le nom pour éviter les doublons (simplifié pour compatibilité H2)
CREATE UNIQUE INDEX idx_products_nom_unique ON products(nom);

-- Fonction et trigger PostgreSQL (désactivés pour H2 en développement)
-- CREATE OR REPLACE FUNCTION update_modified_column()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.date_modification = CURRENT_TIMESTAMP;
--     RETURN NEW;
-- END;
-- $$ language 'plpgsql';

-- CREATE TRIGGER update_products_modified_time
--     BEFORE UPDATE ON products
--     FOR EACH ROW
--     EXECUTE FUNCTION update_modified_column();

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE products IS 'Table des produits du système de gestion de commandes';
COMMENT ON COLUMN products.id IS 'Identifiant unique du produit (clé primaire)';
COMMENT ON COLUMN products.nom IS 'Nom du produit (unique, requis)';
COMMENT ON COLUMN products.description IS 'Description détaillée du produit (optionnel)';
COMMENT ON COLUMN products.prix IS 'Prix unitaire du produit en euros (requis, > 0)';
COMMENT ON COLUMN products.quantite_stock IS 'Quantité disponible en stock (requis, >= 0)';
COMMENT ON COLUMN products.categorie IS 'Catégorie du produit (requis)';
COMMENT ON COLUMN products.date_creation IS 'Date et heure de création du produit';
COMMENT ON COLUMN products.date_modification IS 'Date et heure de dernière modification du produit';

-- Insertion de données de test (optionnel pour le développement)
INSERT INTO products (nom, description, prix, quantite_stock, categorie) VALUES
('Smartphone Samsung Galaxy S24', 'Smartphone Android avec écran AMOLED 6.1 pouces, 128Go', 699.99, 50, 'Électronique'),
('MacBook Pro 14"', 'Ordinateur portable Apple avec puce M3, 16Go RAM, 512Go SSD', 2499.99, 25, 'Informatique'),
('Casque Sony WH-1000XM5', 'Casque sans fil à réduction de bruit active', 349.99, 100, 'Audio'),
('iPad Air', 'Tablette Apple 10.9 pouces avec puce M1, 64Go', 649.99, 75, 'Électronique'),
('Dell XPS 13', 'Ultrabook 13.3 pouces, Intel Core i7, 16Go RAM, 512Go SSD', 1299.99, 30, 'Informatique'),
('AirPods Pro', 'Écouteurs sans fil Apple avec réduction de bruit', 279.99, 200, 'Audio'),
('Monitor LG 27"', 'Écran 4K IPS 27 pouces pour professionnel', 399.99, 40, 'Informatique'),
('Clavier mécanique Logitech', 'Clavier gaming RGB avec switches mécaniques', 129.99, 60, 'Accessoires'),
('Souris Razer DeathAdder', 'Souris gaming haute précision 16000 DPI', 79.99, 80, 'Accessoires'),
('Webcam Logitech C920', 'Webcam HD 1080p pour visioconférence', 99.99, 120, 'Accessoires');

-- Vérification des données insérées
-- SELECT COUNT(*) FROM products;
-- SELECT DISTINCT categorie FROM products ORDER BY categorie;
