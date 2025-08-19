-- Migration V2: Insertion de données d'exemple supplémentaires
-- Auteur: Product Service Team
-- Date: 2024-01-02
-- Description: Ajout de données d'exemple pour le développement et les démonstrations

-- Insertion de catégories d'exemple avec plus de variété
INSERT INTO products (nom, description, prix, quantite_stock, categorie) VALUES
-- Catégorie Informatique - Ordinateurs et composants
('Gaming PC Ultimate', 'PC gaming haut de gamme avec RTX 4080, Intel i9, 32Go RAM, 2To SSD', 2999.99, 8, 'Informatique'),
('MacBook Air M3', 'Ultrabook Apple avec puce M3, 16Go RAM, 512Go SSD, écran Retina 13.6"', 1899.99, 15, 'Informatique'),
('Surface Pro 9', 'Tablette-PC Microsoft 2-en-1, Intel i7, 16Go RAM, 256Go SSD', 1299.99, 20, 'Informatique'),
('ThinkPad X1 Carbon', 'Ultrabook professionnel Lenovo, Intel i7, 16Go RAM, 1To SSD', 1799.99, 12, 'Informatique'),
('iMac 24" M3', 'Ordinateur tout-en-un Apple avec écran Retina 4.5K, puce M3, 16Go RAM', 1699.99, 6, 'Informatique'),

-- Catégorie Électronique - Smartphones et accessoires
('iPhone 15 Pro Max', 'Smartphone Apple avec puce A17 Pro, écran 6.7" ProMotion, 256Go', 1199.99, 45, 'Électronique'),
('Google Pixel 8 Pro', 'Smartphone Google avec IA avancée, écran 6.7" OLED, 128Go', 899.99, 35, 'Électronique'),
('OnePlus 12', 'Smartphone Android flagship avec Snapdragon 8 Gen 3, 256Go', 799.99, 28, 'Électronique'),
('Xiaomi 14 Ultra', 'Smartphone photo professionnel avec capteur 1" Leica, 512Go', 1099.99, 18, 'Électronique'),
('Nothing Phone 2', 'Smartphone transparent avec interface unique, 256Go', 599.99, 22, 'Électronique'),

-- Catégorie Audio - Casques et enceintes
('Bose QuietComfort Ultra', 'Casque premium à réduction de bruit active spatiale', 429.99, 85, 'Audio'),
('Sennheiser HD 800S', 'Casque audiophile haut de gamme pour professionnels', 1599.99, 12, 'Audio'),
('Focal Utopia', 'Casque hifi de référence française, transducteurs Béryllium', 3999.99, 3, 'Audio'),
('Marshall Woburn III', 'Enceinte Bluetooth vintage Marshall, son puissant 130W', 499.99, 25, 'Audio'),
('Sonos Arc', 'Barre de son premium avec Dolby Atmos et contrôle vocal', 899.99, 18, 'Audio'),

-- Catégorie Accessoires - Périphériques et gadgets
('Magic Keyboard', 'Clavier sans fil Apple avec Touch ID et rétroéclairage', 179.99, 40, 'Accessoires'),
('MX Master 3S', 'Souris ergonomique Logitech pour créatifs et professionnels', 109.99, 65, 'Accessoires'),
('Stream Deck', 'Contrôleur Elgato avec 15 touches LCD personnalisables', 149.99, 30, 'Accessoires'),
('DualSense Edge', 'Manette PlayStation 5 pro avec paddles et sticks ajustables', 199.99, 24, 'Accessoires'),
('Apple Pencil Pro', 'Stylet Apple avec retour haptique et rotation pour iPad Pro', 129.99, 55, 'Accessoires'),

-- Catégorie Gaming - Jeux vidéo et consoles
('PlayStation 5 Slim', 'Console de jeu Sony dernière génération, 1To SSD', 549.99, 15, 'Gaming'),
('Xbox Series X', 'Console Microsoft 4K native, 1To SSD, Quick Resume', 499.99, 20, 'Gaming'),
('Nintendo Switch OLED', 'Console portable/salon Nintendo avec écran OLED 7"', 349.99, 35, 'Gaming'),
('Steam Deck OLED', 'Console portable PC gaming Valve avec écran OLED', 649.99, 12, 'Gaming'),
('ASUS ROG Ally', 'Console portable Windows avec AMD Z1 Extreme', 699.99, 8, 'Gaming'),

-- Catégorie Maison - Domotique et électroménager
('HomePod mini', 'Enceinte intelligente Apple avec Siri intégré', 99.99, 75, 'Maison'),
('Nest Hub Max', 'Écran intelligent Google avec caméra et Assistant', 229.99, 32, 'Maison'),
('Philips Hue Starter Kit', 'Kit éclairage connecté RGB avec pont et 3 ampoules', 179.99, 28, 'Maison'),
('Roomba j7+', 'Aspirateur robot iRobot avec vidage automatique', 599.99, 18, 'Maison'),
('Dyson V15 Detect', 'Aspirateur sans fil avec détection laser de poussière', 749.99, 22, 'Maison'),

-- Catégorie Montres - Smartwatches et accessoires
('Apple Watch Ultra 2', 'Montre connectée Apple titanium pour sports extrêmes', 799.99, 25, 'Montres'),
('Galaxy Watch 6 Classic', 'Smartwatch Samsung avec lunette rotative et Wear OS', 399.99, 35, 'Montres'),
('Garmin Epix Pro', 'Montre GPS multisports avec cartographie et AMOLED', 899.99, 15, 'Montres'),
('Fitbit Charge 6', 'Bracelet fitness avec GPS et suivi santé avancé', 199.99, 45, 'Montres'),
('TAG Heuer Connected', 'Montre connectée suisse de luxe avec Wear OS', 1799.99, 8, 'Montres'),

-- Catégorie Photo - Appareils photo et objectifs
('Canon EOS R5', 'Appareil photo hybride 45MP avec vidéo 8K et stabilisation', 3899.99, 6, 'Photo'),
('Sony Alpha 7R V', 'Boîtier hybride haute résolution 61MP avec IA avancée', 3799.99, 5, 'Photo'),
('Fujifilm X-T5', 'Appareil photo APS-C 40MP avec simulation de films', 1699.99, 12, 'Photo'),
('RF 24-70mm f/2.8L IS', 'Objectif zoom Canon professionnel stabilisé', 2299.99, 8, 'Photo'),
('DJI Mavic 3 Pro', 'Drone professionnel avec caméra Hasselblad et télé 166mm', 2199.99, 10, 'Photo'),

-- Catégorie Stockage - Disques durs et SSD
('Samsung 990 Pro 2To', 'SSD NVMe PCIe 4.0 ultra-rapide 7000 MB/s', 199.99, 50, 'Stockage'),
('WD Black SN850X 1To', 'SSD gaming NVMe avec heatsink RGB', 149.99, 65, 'Stockage'),
('Seagate IronWolf 8To', 'Disque dur NAS 7200 RPM pour serveurs', 229.99, 30, 'Stockage'),
('Crucial X8 4To', 'SSD externe portable USB-C ultra-compact', 399.99, 25, 'Stockage'),
('G-Drive 18To', 'Disque dur externe professionnel USB-C Thunderbolt', 499.99, 15, 'Stockage');

-- Mise à jour des statistiques après insertion (PostgreSQL uniquement)
-- ANALYZE products;

-- Vérification des données insérées
-- SELECT categorie, COUNT(*) as nombre_produits, AVG(prix) as prix_moyen
-- FROM products
-- GROUP BY categorie
-- ORDER BY categorie;

-- SELECT COUNT(*) as total_produits,
--        SUM(quantite_stock) as stock_total,
--        MIN(prix) as prix_min,
--        MAX(prix) as prix_max
-- FROM products;
