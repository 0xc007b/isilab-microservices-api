-- Migration V1.2: Compatibilité H2 et fonctionnalités alternatives
-- Auteur: Product Service Team
-- Date: 2024-01-02
-- Description: Implémentation des fonctionnalités compatibles avec H2 pour le développement

-- Cette migration ajoute des fonctionnalités simples compatibles avec H2

-- Index supplémentaires pour améliorer les performances (compatible H2)
CREATE INDEX idx_products_search ON products(nom, categorie);
CREATE INDEX idx_products_stock_category ON products(quantite_stock, categorie);
CREATE INDEX idx_products_date_modified ON products(date_modification);

-- Vue simple pour les statistiques (compatible H2 et PostgreSQL)
CREATE VIEW v_products_stats AS
SELECT
    categorie,
    COUNT(*) as nombre_produits,
    AVG(prix) as prix_moyen,
    SUM(quantite_stock) as stock_total,
    MIN(prix) as prix_min,
    MAX(prix) as prix_max
FROM products
GROUP BY categorie
ORDER BY categorie;

-- Vue pour les produits en stock faible (compatible)
CREATE VIEW v_products_low_stock AS
SELECT
    id,
    nom,
    categorie,
    prix,
    quantite_stock,
    CASE
        WHEN quantite_stock = 0 THEN 'Rupture de stock'
        WHEN quantite_stock <= 5 THEN 'Stock critique'
        WHEN quantite_stock <= 10 THEN 'Stock faible'
        ELSE 'Stock normal'
    END as statut_stock
FROM products
WHERE quantite_stock <= 10
ORDER BY quantite_stock ASC, nom ASC;

-- Commentaires sur les vues
COMMENT ON TABLE v_products_stats IS 'Statistiques des produits par catégorie';
COMMENT ON TABLE v_products_low_stock IS 'Produits avec stock faible';
