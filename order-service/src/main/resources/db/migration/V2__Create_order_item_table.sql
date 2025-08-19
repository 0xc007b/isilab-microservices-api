-- Migration V2: Création de la table des articles de commande (order_items)
-- Auteur: Order Management System
-- Date: 2024-12-19

-- Création de la table order_items
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    prix_total DECIMAL(10,2) NOT NULL,
    date_creation TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT chk_order_items_quantite_positive CHECK (quantite > 0),
    CONSTRAINT chk_order_items_prix_unitaire_positive CHECK (prix_unitaire > 0),
    CONSTRAINT chk_order_items_prix_total_positive CHECK (prix_total >= 0),
    CONSTRAINT chk_order_items_product_id_positive CHECK (product_id > 0)
);

-- Création des index pour optimiser les performances
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
CREATE INDEX idx_order_items_date_creation ON order_items (date_creation);
CREATE INDEX idx_order_items_order_product ON order_items (order_id, product_id);
CREATE INDEX idx_order_items_product_name ON order_items (product_name);

-- Index pour les recherches par date
CREATE INDEX idx_order_items_date_creation_desc ON order_items (date_creation DESC);

-- Index composite pour les statistiques de ventes
CREATE INDEX idx_order_items_product_stats ON order_items (product_id, quantite, prix_total);

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE order_items IS 'Table des articles de commande';
COMMENT ON COLUMN order_items.id IS 'Identifiant unique de l''article';
COMMENT ON COLUMN order_items.order_id IS 'Référence vers la commande parente';
COMMENT ON COLUMN order_items.product_id IS 'Identifiant du produit commandé';
COMMENT ON COLUMN order_items.product_name IS 'Nom du produit au moment de la commande';
COMMENT ON COLUMN order_items.quantite IS 'Quantité commandée';
COMMENT ON COLUMN order_items.prix_unitaire IS 'Prix unitaire du produit au moment de la commande';
COMMENT ON COLUMN order_items.prix_total IS 'Prix total pour cet article (quantité × prix unitaire)';
COMMENT ON COLUMN order_items.date_creation IS 'Date et heure de création de l''article';
COMMENT ON COLUMN order_items.date_modification IS 'Date et heure de dernière modification';
COMMENT ON COLUMN order_items.version IS 'Version pour le contrôle d''optimistic locking';

-- Trigger pour mettre à jour automatiquement la date de modification
CREATE TRIGGER trg_order_items_update_date_modification
    BEFORE UPDATE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_date_modification();

-- Fonction pour calculer automatiquement le prix total
CREATE OR REPLACE FUNCTION calculate_prix_total()
RETURNS TRIGGER AS $$
BEGIN
    NEW.prix_total = NEW.quantite * NEW.prix_unitaire;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_order_items_calculate_prix_total
    BEFORE INSERT OR UPDATE OF quantite, prix_unitaire ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION calculate_prix_total();

-- Fonction pour mettre à jour le montant total de la commande
CREATE OR REPLACE FUNCTION update_order_total()
RETURNS TRIGGER AS $$
BEGIN
    -- Calculer le nouveau montant total de la commande
    UPDATE orders
    SET montant_total = (
        SELECT COALESCE(SUM(prix_total), 0)
        FROM order_items
        WHERE order_id = COALESCE(NEW.order_id, OLD.order_id)
    )
    WHERE id = COALESCE(NEW.order_id, OLD.order_id);

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- Triggers pour maintenir la cohérence du montant total
CREATE TRIGGER trg_order_items_update_order_total_insert
    AFTER INSERT ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_order_total();

CREATE TRIGGER trg_order_items_update_order_total_update
    AFTER UPDATE OF quantite, prix_unitaire, prix_total ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_order_total();

CREATE TRIGGER trg_order_items_update_order_total_delete
    AFTER DELETE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION update_order_total();

-- Fonction pour éviter les doublons de produits dans une même commande
CREATE OR REPLACE FUNCTION prevent_duplicate_products()
RETURNS TRIGGER AS $$
BEGIN
    -- Vérifier s'il existe déjà un article avec le même produit dans cette commande
    IF EXISTS (
        SELECT 1 FROM order_items
        WHERE order_id = NEW.order_id
        AND product_id = NEW.product_id
        AND id != COALESCE(NEW.id, 0)
    ) THEN
        RAISE EXCEPTION 'Le produit % est déjà présent dans cette commande', NEW.product_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_order_items_prevent_duplicates
    BEFORE INSERT OR UPDATE ON order_items
    FOR EACH ROW
    EXECUTE FUNCTION prevent_duplicate_products();

-- Vue pour les statistiques des articles
CREATE VIEW order_items_stats AS
SELECT
    product_id,
    product_name,
    COUNT(*) as nombre_commandes,
    SUM(quantite) as quantite_totale_vendue,
    SUM(prix_total) as chiffre_affaires_total,
    AVG(prix_unitaire) as prix_moyen,
    MIN(date_creation) as premiere_vente,
    MAX(date_creation) as derniere_vente
FROM order_items
GROUP BY product_id, product_name;

COMMENT ON VIEW order_items_stats IS 'Vue des statistiques des articles groupées par produit';

-- Vue pour les détails des commandes avec articles
CREATE VIEW order_details AS
SELECT
    o.id as order_id,
    o.client_id,
    o.date_commande,
    o.statut as order_status,
    o.montant_total as order_total,
    oi.id as item_id,
    oi.product_id,
    oi.product_name,
    oi.quantite,
    oi.prix_unitaire,
    oi.prix_total as item_total
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id;

COMMENT ON VIEW order_details IS 'Vue détaillée des commandes avec leurs articles';

-- Index sur les vues matérialisées si nécessaire (pour de gros volumes)
-- CREATE MATERIALIZED VIEW mv_product_sales_summary AS
-- SELECT
--     product_id,
--     product_name,
--     COUNT(DISTINCT order_id) as distinct_orders,
--     SUM(quantite) as total_quantity_sold,
--     SUM(prix_total) as total_revenue,
--     AVG(prix_unitaire) as average_unit_price
-- FROM order_items
-- GROUP BY product_id, product_name;

-- CREATE UNIQUE INDEX idx_mv_product_sales_product_id ON mv_product_sales_summary (product_id);
