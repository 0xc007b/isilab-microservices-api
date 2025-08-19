# Order Management System

![Order Management System](assets/images/banner-top.png)

Un système de gestion de commandes basé sur une architecture microservices avec Spring Boot et Spring Cloud. Le système expose des APIs REST pour la gestion des clients, produits et commandes avec découverte de services via Eureka et routage via API Gateway.

## Architecture Microservices

Le système est composé de 5 services principaux :
- `Eureka Server` - Service de découverte et registre des services
- `API Gateway` - Point d'entrée unique et routage des requêtes
- `Customer Service` - Gestion des clients et de leurs informations
- `Product Service` - Gestion des produits et inventaire
- `Order Service` - Gestion des commandes et orchestration métier

## Tech Stack

- **Java 17**
- **Spring Boot 3.5.4** (Web, Data JPA, Validation, Actuator)
- **Spring Cloud 2025.0.0** (Gateway, Eureka, OpenFeign, LoadBalancer)
- **PostgreSQL 15** (bases de données séparées par service)
- **Flyway** (migrations de base de données)
- **SpringDoc OpenAPI 3** (documentation Swagger UI)
- **TestContainers** (tests d'intégration)
- **Docker & Docker Compose** (orchestration et déploiement)
- **Maven** (gestion des dépendances et build)

## Configuration

### Ports des Services

- **API Gateway**: `8080`
- **Eureka Server**: `8761`
- **Customer Service**: `8081`
- **Product Service**: `8082`
- **Order Service**: `8083`

### Bases de Données PostgreSQL

- **Customer DB**: `localhost:5432` (user: `client_user`, db: `client_db`)
- **Product DB**: `localhost:5433` (user: `product_user`, db: `product_db`)
- **Order DB**: `localhost:5434` (user: `order_user`, db: `order_db`)

### Variables d'Environnement

Principales variables configurables via l'environnement :

```bash
# Eureka Configuration
EUREKA_SERVER_URL=http://eureka-server:8761/eureka/

# Database Configuration (par service)
DB_USERNAME=<user>
DB_PASSWORD=<password>

# Service URLs (pour communication inter-services)
CUSTOMER_SERVICE_URL=http://customer-service:8081
PRODUCT_SERVICE_URL=http://product-service:8082

# Logging
LOG_LEVEL=INFO
SQL_LOG_LEVEL=WARN

# Profils Spring
SPRING_PROFILES_ACTIVE=docker
```

## Lancer avec Docker Compose

**Prérequis :**
- Docker et Docker Compose installés
- Maven 3.8+ pour construire les JARs

### Étapes de démarrage :

1) **Construire tous les services :**
~~~bash
# Dans chaque dossier de service (eureka-server, api-gateway, customer-service, product-service, order-service)
mvn clean package -DskipTests

~~~

2) **Démarrer l'écosystème complet :**
~~~bash
docker compose up -d
~~~

3) **Vérifier le démarrage des services :**
~~~bash
# Vérifier les logs
docker compose logs -f

# Vérifier le statut des services
docker compose ps
~~~

4) **URLs d'accès :**
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Customer Service**: http://localhost:8081 (via Gateway: http://localhost:8080/customers)
- **Product Service**: http://localhost:8082 (via Gateway: http://localhost:8080/products)
- **Order Service**: http://localhost:8083 (via Gateway: http://localhost:8080/orders)

### Gestion Docker :

**Arrêter les services :**
~~~bash
docker compose down
~~~

**Réinitialiser avec suppression des données :**
~~~bash
docker compose down -v
~~~

**Reconstruire un service spécifique :**
~~~bash
docker compose build <service-name>
docker compose up -d <service-name>
~~~

## Lancer localement (sans Docker)

### Prérequis :
1. **PostgreSQL installé et configuré**
2. **Créer les bases de données :**

~~~sql
-- Base Customer Service
CREATE DATABASE client_db;
CREATE USER client_user WITH ENCRYPTED PASSWORD 'client_pass';
GRANT ALL PRIVILEGES ON DATABASE client_db TO client_user;

-- Base Product Service
CREATE DATABASE product_db;
CREATE USER product_user WITH ENCRYPTED PASSWORD 'product_pass';
GRANT ALL PRIVILEGES ON DATABASE product_db TO product_user;

-- Base Order Service
CREATE DATABASE order_db;
CREATE USER order_user WITH ENCRYPTED PASSWORD 'order_pass';
GRANT ALL PRIVILEGES ON DATABASE order_db TO order_user;
~~~

### Ordre de démarrage des services :

1) **Eureka Server** (obligatoire en premier) :
~~~bash
cd eureka-server
mvn spring-boot:run
~~~

2) **API Gateway** :
~~~bash
cd api-gateway
mvn spring-boot:run
~~~

3) **Services métier** (peuvent être lancés en parallèle) :
~~~bash
# Terminal 1 - Customer Service
cd customer-service
mvn spring-boot:run

# Terminal 2 - Product Service
cd product-service
mvn spring-boot:run

# Terminal 3 - Order Service
cd order-service
mvn spring-boot:run
~~~

### Configuration locale :

Exporter les variables d'environnement (Windows PowerShell) :
~~~powershell
$env:EUREKA_SERVER_URL="http://localhost:8761/eureka/"
$env:DB_USERNAME="<appropriate_user>"
$env:DB_PASSWORD="<appropriate_password>"
$env:SPRING_PROFILES_ACTIVE="dev"
~~~

## Modèle de Données

### Customer (Service Client)
```java
@Entity
public class Customer {
    @Id @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;
    private String email;       // unique
    private String phoneNumber;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Product (Service Produit)
```java
@Entity
public class Product {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String sku;         // unique
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Order & OrderItem (Service Commande)
```java
@Entity
public class Order {
    @Id @GeneratedValue
    private Long id;
    private Long customerId;    // référence vers Customer Service
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus status; // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}

@Entity
public class OrderItem {
    @Id @GeneratedValue
    private Long id;
    private Long productId;     // référence vers Product Service
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    @ManyToOne
    private Order order;
}
```

## Architecture DTOs et Mappers

Le projet utilise une architecture en couches avec séparation claire des DTOs :

### DTOs par Service
- **CustomerDto** : `CreateRequest`, `UpdateRequest`, `Response`
- **ProductDto** : `CreateRequest`, `UpdateRequest`, `Response`, `StockUpdateRequest`
- **OrderDto** : `CreateRequest`, `Response`, `StatusUpdateRequest`
- **OrderItemDto** : DTOs pour les éléments de commande

### Communication Inter-Services
- **Feign Clients** pour la communication synchrone entre services
- **Circuit Breaker** intégré pour la résilience
- **Load Balancing** automatique via Eureka

## API Documentation

### Base URLs
- **Via Gateway**: `http://localhost:8080`
- **Eureka Dashboard**: `http://localhost:8761`
- **Direct Services**: `http://localhost:808[1-3]`

### Customer Service API

**Via Gateway**: `/customers`

- `GET /customers` → Liste des clients
- `GET /customers/{id}` → Détail d'un client
- `POST /customers` → Créer un client
- `PUT /customers/{id}` → Mettre à jour un client
- `DELETE /customers/{id}` → Supprimer un client

**Exemple de création de client :**
~~~json
POST /customers
{
  "firstName": "Jean",
  "lastName": "Dupont",
  "email": "jean.dupont@email.com",
  "phoneNumber": "+33123456789",
  "address": "123 Rue de la Paix, Paris"
}
~~~

### Product Service API

**Via Gateway**: `/products`

- `GET /products` → Liste des produits
- `GET /products/{id}` → Détail d'un produit
- `POST /products` → Créer un produit
- `PUT /products/{id}` → Mettre à jour un produit
- `DELETE /products/{id}` → Supprimer un produit
- `PUT /products/{id}/stock` → Mettre à jour le stock

**Exemple de création de produit :**
~~~json
POST /products
{
  "name": "Laptop Dell XPS 13",
  "description": "Ordinateur portable haute performance",
  "price": 1299.99,
  "stockQuantity": 50,
  "category": "Electronics",
  "sku": "DELL-XPS13-001"
}
~~~

### Order Service API

**Via Gateway**: `/orders`

- `GET /orders` → Liste des commandes
- `GET /orders/{id}` → Détail d'une commande
- `POST /orders` → Créer une commande
- `PUT /orders/{id}/status` → Changer le statut
- `GET /orders/customer/{customerId}` → Commandes d'un client

**Exemple de création de commande :**
~~~json
POST /orders
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
~~~

## Exemples cURL

### Via API Gateway

**Créer un client :**
~~~bash
curl -X POST http://localhost:8080/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Marie",
    "lastName": "Martin",
    "email": "marie.martin@email.com",
    "phoneNumber": "+33987654321",
    "address": "456 Avenue des Champs, Lyon"
  }'
~~~

**Créer un produit :**
~~~bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphone iPhone 15",
    "description": "Dernière génération smartphone Apple",
    "price": 999.00,
    "stockQuantity": 25,
    "category": "Mobile",
    "sku": "APPLE-IP15-128"
  }'
~~~

**Créer une commande :**
~~~bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {"productId": 1, "quantity": 1},
      {"productId": 2, "quantity": 2}
    ]
  }'
~~~

**Consulter les commandes d'un client :**
~~~bash
curl -X GET "http://localhost:8080/orders/customer/1"
~~~

## Gestion des Erreurs et Validation

### Validation automatique
- Validation via **Jakarta Bean Validation** sur tous les DTOs
- **GlobalExceptionHandler** dans chaque service pour une gestion cohérente des erreurs

### Types d'erreurs gérées
- **400 Bad Request** : Données invalides ou validation échouée
- **404 Not Found** : Ressource non trouvée
- **409 Conflict** : Conflit métier (email déjà existant, stock insuffisant, etc.)
- **500 Internal Server Error** : Erreurs techniques
- **503 Service Unavailable** : Service indisponible (circuit breaker)

**Exemple de réponse d'erreur :**
~~~json
{
  "timestamp": "2024-01-15T10:30:45Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/orders",
  "details": {
    "fieldErrors": {
      "customerId": "must not be null",
      "items": "must not be empty"
    }
  }
}
~~~

## Monitoring et Observabilité

### Spring Boot Actuator
Chaque service expose des endpoints de monitoring :

- `/actuator/health` - État de santé du service
- `/actuator/info` - Informations sur l'application
- `/actuator/metrics` - Métriques applicatives
- `/actuator/prometheus` - Métriques au format Prometheus

### Eureka Dashboard
- **URL**: http://localhost:8761
- Vue d'ensemble de tous les services enregistrés
- État de santé et métadonnées des instances

### Logging
- Configuration de logs structurés par environnement
- Niveaux de logs configurables via variables d'environnement
- Rotation automatique des fichiers de logs

## Documentation API Interactive (Swagger)

Chaque service expose sa documentation Swagger :

- **Customer Service**: http://localhost:8081/swagger-ui.html
- **Product Service**: http://localhost:8082/swagger-ui.html
- **Order Service**: http://localhost:8083/swagger-ui.html

**Via API Gateway** (recommandé) :
- http://localhost:8080/customers/swagger-ui.html
- http://localhost:8080/products/swagger-ui.html
- http://localhost:8080/orders/swagger-ui.html

### Fonctionnalités Swagger
- Interface interactive pour tester tous les endpoints
- Documentation complète des schémas de données
- Exemples de requêtes et réponses
- Validation en temps réel des paramètres

## Profils de Configuration

### Profils disponibles par service

**Développement (`dev`)** :
- Base de données H2 en mémoire
- Logs détaillés activés
- H2 Console accessible
- DDL auto en mode `create-drop`

**Test (`test`)** :
- Configuration optimisée pour les tests unitaires/intégration
- TestContainers pour les tests avec PostgreSQL
- Eureka désactivé
- Logs minimaux

**Production (`prod`)** :
- Configuration optimisée pour la performance
- Logs limités aux informations essentielles
- Validation stricte des migrations Flyway
- Health checks sécurisés

**Docker (`docker`)** :
- Configuration pour l'orchestration Docker Compose
- URLs de services internes au réseau Docker
- Timeouts adaptés à l'environnement conteneurisé

## Tests

### Structure des tests
Chaque service implémente une suite complète de tests :

**Tests unitaires** :
- Controllers (MockMvc)
- Services (logique métier)
- Mappers (conversions DTO/Entité)

**Tests d'intégration** :
- Repository layer avec TestContainers
- Communication inter-services avec WireMock
- Tests end-to-end via TestRestTemplate

**Commandes de test** :
~~~bash
# Tests unitaires uniquement
mvn test

# Tests d'intégration avec TestContainers
mvn verify

# Tests avec couverture de code
mvn test jacoco:report
~~~

## Développement

### Prérequis développeur
- **Java 17+**
- **Maven 3.8+**
- **Docker** (pour TestContainers et déploiement)
- **PostgreSQL** (pour développement local)
- **IDE** avec support Lombok et annotations processing

### Workflow de développement

1. **Démarrer Eureka Server** (toujours en premier)
2. **Développer/modifier un service** en mode `dev`
3. **Utiliser H2** pour prototypage rapide
4. **Tester avec PostgreSQL** avant commit
5. **Lancer les tests d'intégration** avec TestContainers

### Scripts utiles

**Build complet** :
~~~bash
# Build de tous les services
for service in eureka-server api-gateway customer-service product-service order-service; do
  echo "Building $service..."
  (cd $service && mvn clean package -DskipTests)
done
~~~

**Nettoyage Docker** :
~~~bash
# Nettoyer les images non utilisées
docker system prune -f

# Reconstruire tous les services
docker compose build --no-cache
~~~

## Déploiement

### Environnement Docker (développement/test)
~~~bash
# Démarrage complet
docker compose up -d

# Scaling d'un service
docker compose up -d --scale customer-service=2

# Mise à jour d'un service
docker compose build customer-service
docker compose up -d customer-service
~~~

### Ordre de démarrage critique
1. **Bases de données PostgreSQL** (avec health checks)
2. **Eureka Server** (service discovery)
3. **API Gateway** (routage)
4. **Services métier** (peuvent démarrer en parallèle)

### Variables d'environnement de production
~~~bash
# Profil de production
SPRING_PROFILES_ACTIVE=prod

# Configuration base de données sécurisée
DB_USERNAME=${DB_USER_SECRET}
DB_PASSWORD=${DB_PASSWORD_SECRET}

# URLs des services
EUREKA_SERVER_URL=http://eureka:8761/eureka/
CUSTOMER_SERVICE_URL=http://customer-service:8081
PRODUCT_SERVICE_URL=http://product-service:8082

# Monitoring
LOG_LEVEL=INFO
METRICS_ENABLED=true
~~~

## Troubleshooting

### Problèmes courants

**Service ne se connecte pas à Eureka** :
- Vérifier que Eureka Server est démarré
- Contrôler la variable `EUREKA_SERVER_URL`
- Vérifier les logs de connexion réseau

**Erreurs de base de données** :
- Vérifier que PostgreSQL est démarré et accessible
- Contrôler les credentials et URLs de connexion
- Vérifier que les migrations Flyway sont à jour

**Communication inter-services échoue** :
- Vérifier l'enregistrement des services dans Eureka
- Contrôler les configurations Feign et timeouts
- Vérifier les logs des Circuit Breakers

**Performance dégradée** :
- Monitor les métriques Actuator (`/actuator/metrics`)
- Vérifier les pools de connexions base de données
- Contrôler la mémoire JVM disponible

### Commandes de diagnostic

**Vérifier les services Eureka** :
~~~bash
curl http://localhost:8761/eureka/apps
~~~

**Health check complet** :
~~~bash
# Via Gateway
curl http://localhost:8080/customers/actuator/health
curl http://localhost:8080/products/actuator/health
curl http://localhost:8080/orders/actuator/health
~~~

**Logs de service spécifique** :
~~~bash
docker compose logs -f <service-name>
~~~

---

## Contribution

1. **Fork** le projet
2. **Créer une branche** feature (`git checkout -b feature/amazing-feature`)
3. **Commiter** les changements (`git commit -m 'Add amazing feature'`)
4. **Push** la branche (`git push origin feature/amazing-feature`)
5. **Ouvrir une Pull Request**

## License

Ce projet est sous license MIT. Voir le fichier `LICENSE` pour plus de détails.

## Support

Pour toute question ou problème :
- **Issues GitHub** : [Créer un issue](https://github.com/your-org/order-management-system/issues)
- **Documentation** : Swagger UI disponible sur chaque service
- **Monitoring** : Dashboards Actuator et Eureka disponibles

---

**Équipe de développement** : Backend Team
**Version** : 1.0.0
**Dernière mise à jour** : Janvier 2024
