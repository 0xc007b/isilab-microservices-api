-- Migration V1: Création de la table des commandes (orders)
-- Auteur: Order Management System
-- Date: 2024-12-19

-- Création de la table orders
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    date_commande TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    montant_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    statut VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    commentaire VARCHAR(500),
    version BIGINT DEFAULT 0,
    CONSTRAINT chk_orders_montant_total_positive CHECK (montant_total >= 0),
    CONSTRAINT chk_orders_statut_valid CHECK (
        statut IN ('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')
    ),
    CONSTRAINT chk_orders_client_id_positive CHECK (client_id > 0)
);

-- Création des index pour optimiser les performances
CREATE INDEX idx_orders_client_id ON orders (client_id);
CREATE INDEX idx_orders_date_commande ON orders (date_commande);
CREATE INDEX idx_orders_statut ON orders (statut);
CREATE INDEX idx_orders_date_commande_desc ON orders (date_commande DESC);
CREATE INDEX idx_orders_client_statut ON orders (client_id, statut);
CREATE INDEX idx_orders_date_statut ON orders (date_commande, statut);

-- Création d'un index composite pour les requêtes de pagination
CREATE INDEX idx_orders_pagination ON orders (date_commande DESC, id);

-- Commentaires sur la table et les colonnes
COMMENT ON TABLE orders IS 'Table des commandes du système de gestion des commandes';
COMMENT ON COLUMN orders.id IS 'Identifiant unique de la commande';
COMMENT ON COLUMN orders.client_id IS 'Identifiant du client qui a passé la commande';
COMMENT ON COLUMN orders.date_commande IS 'Date et heure de création de la commande';
COMMENT ON COLUMN orders.date_modification IS 'Date et heure de dernière modification';
COMMENT ON COLUMN orders.montant_total IS 'Montant total de la commande en euros';
COMMENT ON COLUMN orders.statut IS 'Statut actuel de la commande (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED)';
COMMENT ON COLUMN orders.commentaire IS 'Commentaire optionnel sur la commande';
COMMENT ON COLUMN orders.version IS 'Version pour le contrôle d''optimistic locking';

-- Trigger pour mettre à jour automatiquement la date de modification
CREATE OR REPLACE FUNCTION update_date_modification()
RETURNS TRIGGER AS $$
BEGIN
    NEW.date_modification = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_orders_update_date_modification
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_date_modification();

-- Fonction pour valider les transitions de statut
CREATE OR REPLACE FUNCTION validate_status_transition()
RETURNS TRIGGER AS $$
BEGIN
    -- Vérifier si c'est une insertion (pas de validation nécessaire)
    IF TG_OP = 'INSERT' THEN
        RETURN NEW;
    END IF;

    -- Vérifier les transitions de statut valides
    IF OLD.statut = 'PENDING' AND NEW.statut NOT IN ('CONFIRMED', 'CANCELLED') THEN
        RAISE EXCEPTION 'Transition de statut invalide: de PENDING vers %', NEW.statut;
    END IF;

    IF OLD.statut = 'CONFIRMED' AND NEW.statut NOT IN ('PROCESSING', 'CANCELLED') THEN
        RAISE EXCEPTION 'Transition de statut invalide: de CONFIRMED vers %', NEW.statut;
    END IF;

    IF OLD.statut = 'PROCESSING' AND NEW.statut NOT IN ('SHIPPED', 'CANCELLED') THEN
        RAISE EXCEPTION 'Transition de statut invalide: de PROCESSING vers %', NEW.statut;
    END IF;

    IF OLD.statut = 'SHIPPED' AND NEW.statut != 'DELIVERED' THEN
        RAISE EXCEPTION 'Transition de statut invalide: de SHIPPED vers %', NEW.statut;
    END IF;

    IF OLD.statut IN ('DELIVERED', 'CANCELLED') THEN
        RAISE EXCEPTION 'Impossible de modifier une commande avec le statut %', OLD.statut;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_orders_validate_status_transition
    BEFORE UPDATE OF statut ON orders
    FOR EACH ROW
    EXECUTE FUNCTION validate_status_transition();

-- Vue pour les statistiques rapides des commandes
CREATE VIEW orders_stats AS
SELECT
    statut,
    COUNT(*) as nombre_commandes,
    SUM(montant_total) as montant_total,
    AVG(montant_total) as montant_moyen,
    MIN(date_commande) as premiere_commande,
    MAX(date_commande) as derniere_commande
FROM orders
GROUP BY statut;

COMMENT ON VIEW orders_stats IS 'Vue des statistiques des commandes groupées par statut';
