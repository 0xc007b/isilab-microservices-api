# Product Service

Service de gestion des produits pour le système de gestion de commandes. Ce microservice gère l'inventaire des produits, leurs caractéristiques et leur stock.

## 📋 Table des matières

- [Fonctionnalités](#fonctionnalités)
- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Utilisation](#utilisation)
- [API Documentation](#api-documentation)
- [Base de données](#base-de-données)
- [Docker](#docker)
- [Tests](#tests)
- [Monitoring](#monitoring)
- [Contribution](#contribution)

## 🚀 Fonctionnalités

### Gestion des produits
- ✅ Création, modification, suppression de produits
- ✅ Recherche multicritères (nom, catégorie, prix, stock)
- ✅ Gestion des catégories
- ✅ Pagination et tri des résultats

### Gestion des stocks
- ✅ Mise à jour du stock (set, increment, decrement)
- ✅ Vérification de disponibilité
- ✅ Alertes stock faible
- ✅ Historique des mouvements de stock

### Fonctionnalités avancées
- ✅ Validation des données (Bean Validation)
- ✅ Gestion centralisée des exceptions
- ✅ Documentation API (OpenAPI/Swagger)
- ✅ Health checks et métriques
- ✅ Support multi-profils (dev, test, prod, docker)

## 🛠 Technologies

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Data JPA** - Accès aux données
- **Spring Cloud Netflix Eureka** - Service discovery
- **PostgreSQL** - Base de données principale
- **H2** - Base de données de test
- **Flyway** - Migrations de base de données
- **OpenAPI 3** - Documentation API
- **Docker** - Conteneurisation
- **Maven** - Gestionnaire de dépendances

## 📋 Prérequis

- Java 17 ou supérieur
- Maven 3.8+ ou utiliser le wrapper Maven inclus
- PostgreSQL 15+ (pour la production)
- Docker et Docker Compose (optionnel)

## 🚀 Installation

### 1. Cloner le projet
```bash
git clone <repository-url>
cd order-management-system/product-service
```

### 2. Configuration de la base de données

#### Pour le développement (H2 - en mémoire)
```bash
# Profil dev activé par défaut
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Pour la production (PostgreSQL)
```bash
# Créer la base de données PostgreSQL
createdb product_db
createuser product_user

# Configurer les variables d'environnement
export DB_USERNAME=product_user
export DB_PASSWORD=product_password
export SPRING_PROFILES_ACTIVE=prod
```

### 3. Compilation et lancement
```bash
# Compilation
./mvnw clean compile

# Tests
./mvnw test

# Lancement
./mvnw spring-boot:run
```

## ⚙️ Configuration

### Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `SPRING_PROFILES_ACTIVE` | Profil Spring actif | `dev` |
| `PORT` | Port du serveur | `8082` |
| `DB_USERNAME` | Utilisateur base de données | `product_user` |
| `DB_PASSWORD` | Mot de passe base de données | `product_password` |
| `EUREKA_SERVER_URL` | URL du serveur Eureka | `http://localhost:8761/eureka/` |
| `LOG_LEVEL` | Niveau de log | `INFO` |

### Profils disponibles

- **dev** : Développement avec H2 en mémoire
- **test** : Tests automatisés
- **prod** : Production avec PostgreSQL
- **docker** : Déploiement Docker

## 📖 Utilisation

### Démarrage du service

```bash
# Avec Maven
./mvnw spring-boot:run

# Avec Java directement
java -jar target/product-service-0.0.1-SNAPSHOT.jar

# Avec profil spécifique
java -jar target/product-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Endpoints principaux

Le service sera disponible sur `http://localhost:8082`

- **API REST** : `http://localhost:8082/api/products`
- **Documentation** : `http://localhost:8082/swagger-ui.html`
- **Health Check** : `http://localhost:8082/actuator/health`
- **Métriques** : `http://localhost:8082/actuator/metrics`

## 📚 API Documentation

### Endpoints disponibles

#### Gestion des produits
```http
GET    /api/products              # Liste des produits (paginée)
GET    /api/products/all          # Tous les produits
GET    /api/products/{id}         # Produit par ID
POST   /api/products              # Créer un produit
PUT    /api/products/{id}         # Modifier un produit
DELETE /api/products/{id}         # Supprimer un produit
```

#### Gestion du stock
```http
PUT    /api/products/{id}/stock              # Mettre à jour le stock
PUT    /api/products/{id}/stock/increment    # Incrémenter le stock
PUT    /api/products/{id}/stock/decrement    # Décrémenter le stock
GET    /api/products/{id}/stock/check        # Vérifier la disponibilité
```

#### Recherche et filtrage
```http
GET    /api/products/search                  # Recherche multicritères
GET    /api/products/category/{categorie}    # Produits par catégorie
GET    /api/products/low-stock              # Produits en stock faible
```

#### Métadonnées
```http
GET    /api/products/categories             # Liste des catégories
GET    /api/products/stats/by-category     # Statistiques par catégorie
GET    /api/products/{id}/exists           # Vérifier l'existence
```

### Exemples de requêtes

#### Créer un produit
```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Smartphone XYZ",
    "description": "Smartphone dernière génération",
    "prix": 699.99,
    "quantiteStock": 100,
    "categorie": "Électronique"
  }'
```

#### Recherche multicritères
```bash
curl "http://localhost:8082/api/products/search?nom=smartphone&prixMin=500&prixMax=1000&page=0&size=10"
```

#### Mettre à jour le stock
```bash
curl -X PUT http://localhost:8082/api/products/1/stock/decrement \
  -H "Content-Type: application/json" \
  -d '{"quantite": 5}'
```

### Documentation interactive

Accédez à la documentation Swagger : `http://localhost:8082/swagger-ui.html`

## 🗃️ Base de données

### Schéma de la table `products`

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    prix DECIMAL(10,2) NOT NULL CHECK (prix > 0),
    quantite_stock INTEGER NOT NULL DEFAULT 0 CHECK (quantite_stock >= 0),
    categorie VARCHAR(100) NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Migrations Flyway

Les migrations sont dans `src/main/resources/db/migration/`:
- `V1__Create_product_table.sql` : Création de la table et des index

### Console H2 (développement)

En mode développement : `http://localhost:8082/h2-console`
- URL : `jdbc:h2:mem:product_db`
- Username : `sa`
- Password : (vide)

## 🐳 Docker

### Construction de l'image

```bash
# Construction
docker build -t product-service:1.0.0 .

# Lancement
docker run -p 8082:8082 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e EUREKA_SERVER_URL=http://eureka-server:8761/eureka/ \
  product-service:1.0.0
```

### Docker Compose

```yaml
version: '3.8'
services:
  postgres-product:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: product_db
      POSTGRES_USER: product_user
      POSTGRES_PASSWORD: product_password
    ports:
      - "5433:5432"

  product-service:
    build: .
    ports:
      - "8082:8082"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      EUREKA_SERVER_URL: http://eureka-server:8761/eureka/
    depends_on:
      - postgres-product
      - eureka-server
```

## 🧪 Tests

### Lancement des tests

```bash
# Tous les tests
./mvnw test

# Tests d'une classe spécifique
./mvnw test -Dtest=ProductServiceTest

# Tests d'intégration
./mvnw test -Dtest=ProductControllerTest
```

### Types de tests

- **Tests unitaires** : Services et mappers
- **Tests d'intégration** : Contrôleurs et repositories
- **Tests de contrat** : Validation des DTOs

## 📊 Monitoring

### Health Checks

```bash
# Health check général
curl http://localhost:8082/actuator/health

# Détails de santé (si autorisé)
curl http://localhost:8082/actuator/health/detailed
```

### Métriques

```bash
# Métriques générales
curl http://localhost:8082/actuator/metrics

# Métrique spécifique
curl http://localhost:8082/actuator/metrics/jvm.memory.used

# Métriques Prometheus
curl http://localhost:8082/actuator/prometheus
```

### Informations du service

```bash
# Informations générales
curl http://localhost:8082/actuator/info

# Variables d'environnement
curl http://localhost:8082/actuator/env
```

## 🔧 Développement

### Structure du projet

```
src/
├── main/
│   ├── java/com/flrxnt/product/
│   │   ├── controller/         # Contrôleurs REST
│   │   ├── service/           # Services métier
│   │   ├── repository/        # Repositories JPA
│   │   ├── entity/           # Entités JPA
│   │   ├── dto/              # DTOs de transfert
│   │   ├── mapper/           # Mappers entité-DTO
│   │   ├── exception/        # Exceptions métier
│   │   └── config/           # Configuration
│   └── resources/
│       ├── db/migration/     # Scripts Flyway
│       └── application.yml   # Configuration
└── test/                     # Tests
```

### Conventions de code

- **Naming** : CamelCase pour les classes, camelCase pour les variables
- **Packages** : Organisation par couches techniques
- **Logs** : Utiliser SLF4J avec des niveaux appropriés
- **Exceptions** : Exceptions métier spécifiques avec gestion centralisée
- **DTOs** : Séparation claire entre entités et DTOs

### Bonnes pratiques

1. **Validation** : Utiliser `@Valid` et Bean Validation
2. **Transactions** : `@Transactional` approprié selon le contexte
3. **Logs** : Messages informatifs avec niveaux appropriés
4. **Tests** : Couverture des cas nominaux et d'erreur
5. **Documentation** : Annotations OpenAPI sur les contrôleurs

## 🤝 Contribution

1. Fork du projet
2. Création d'une branche feature (`git checkout -b feature/new-feature`)
3. Commit des changements (`git commit -am 'Add new feature'`)
4. Push de la branche (`git push origin feature/new-feature`)
5. Création d'une Pull Request

### Checklist avant PR

- [ ] Tests passent (`./mvnw test`)
- [ ] Code formaté et propre
- [ ] Documentation mise à jour
- [ ] Logs appropriés ajoutés
- [ ] Health checks fonctionnels

## 📝 Changelog

### Version 1.0.0
- ✅ CRUD complet des produits
- ✅ Gestion des stocks
- ✅ Recherche multicritères
- ✅ Documentation API
- ✅ Support Docker
- ✅ Health checks et métriques

## 📞 Support

Pour toute question ou problème :

- **Email** : backend@orderms.com
- **Documentation** : [Swagger UI](http://localhost:8082/swagger-ui.html)
- **Logs** : Vérifier `logs/product-service.log`

---

**Product Service** - Système de gestion de commandes  
Version 1.0.0 - Équipe Backend