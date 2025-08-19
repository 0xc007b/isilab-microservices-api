# Customer Service - Microservice de Gestion des Clients

## 📋 Description

Le **Customer Service** est un microservice dédié à la gestion des clients dans l'architecture de microservices du système de gestion de commandes. Il fournit une API RESTful complète pour créer, lire, mettre à jour et supprimer des clients.

## 🏗️ Architecture

Ce service fait partie d'une architecture microservices et s'intègre avec :
- **Eureka Server** (Service Discovery)
- **API Gateway** (Point d'entrée unique)
- **PostgreSQL** (Base de données principale)
- **H2 Database** (Base de données pour tests et développement)

## 🚀 Fonctionnalités

### APIs REST Disponibles

#### Gestion des Clients
- `POST /api/customers` - Créer un nouveau client
- `GET /api/customers/{id}` - Récupérer un client par ID
- `GET /api/customers/email/{email}` - Récupérer un client par email
- `PUT /api/customers/{id}` - Mettre à jour un client
- `DELETE /api/customers/{id}` - Supprimer un client

#### Recherche et Listage
- `GET /api/customers` - Lister tous les clients (avec pagination)
- `GET /api/customers/search/name?name=xxx` - Rechercher par nom
- `GET /api/customers/search/city?city=xxx` - Rechercher par ville
- `GET /api/customers/search?name=xxx&email=xxx&city=xxx` - Recherche multicritères

#### Utilitaires
- `GET /api/customers/{id}/exists` - Vérifier l'existence d'un client
- `GET /api/customers/email/{email}/exists` - Vérifier l'existence d'un email
- `GET /api/customers/count` - Compter le nombre total de clients
- `PUT /api/customers/{id}/status` - Changer le statut d'un client

### Fonctionnalités Techniques
- ✅ Validation des données d'entrée
- ✅ Gestion centralisée des erreurs
- ✅ Documentation API avec Swagger/OpenAPI
- ✅ Monitoring avec Spring Actuator
- ✅ Migrations de base de données avec Flyway
- ✅ Support de la pagination et du tri
- ✅ Logs structurés
- ✅ Containerisation Docker

## 🛠️ Technologies Utilisées

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **Spring Cloud Netflix Eureka Client**
- **Spring Cloud OpenFeign**
- **PostgreSQL** (Production)
- **H2 Database** (Développement/Tests)
- **Flyway** (Migrations DB)
- **SpringDoc OpenAPI** (Documentation)
- **Maven** (Build)
- **Docker** (Containerisation)

## 🔧 Configuration

### Variables d'Environnement

| Variable | Description | Valeur par défaut |
|----------|-------------|------------------|
| `SPRING_PROFILES_ACTIVE` | Profil Spring actif | `prod` |
| `SERVER_PORT` | Port du serveur | `8081` |
| `DATABASE_URL` | URL de la base de données | `jdbc:postgresql://localhost:5432/customer_db` |
| `DATABASE_USERNAME` | Nom d'utilisateur DB | `customer_user` |
| `DATABASE_PASSWORD` | Mot de passe DB | `customer_password` |
| `EUREKA_URL` | URL du serveur Eureka | `http://eureka-server:8761/eureka/` |
| `JAVA_OPTS` | Options JVM | `-Xmx512m -Xms256m` |

### Profils Spring

#### Développement (`dev`)
- Base de données H2 en mémoire
- Console H2 activée sur `/h2-console`
- Logs détaillés
- Flyway désactivé (DDL auto)

#### Test (`test`)
- Base de données H2 en mémoire
- Logs minimaux
- Configuration optimisée pour les tests

#### Production (`prod`)
- Base de données PostgreSQL
- Logs optimisés pour la production
- Monitoring complet activé

## 🚀 Démarrage

### Prérequis
- Java 17+
- Maven 3.8+
- PostgreSQL 15+ (pour la production)
- Docker (optionnel)

### Démarrage en Local

1. **Cloner le repository**
```bash
git clone <repository-url>
cd customer-service
```

2. **Démarrer avec le profil dev (H2)**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

3. **Démarrer avec PostgreSQL**
```bash
# Démarrer PostgreSQL et créer la base
createdb customer_db
createuser customer_user

# Démarrer l'application
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Démarrage avec Docker

1. **Construire l'image**
```bash
docker build -t customer-service:latest .
```

2. **Démarrer le conteneur**
```bash
docker run -d \
  --name customer-service \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=dev \
  customer-service:latest
```

## 📊 Endpoints de Monitoring

- **Health Check** : `GET /actuator/health`
- **Metrics** : `GET /actuator/metrics`
- **Info** : `GET /actuator/info`
- **Documentation API** : `GET /swagger-ui.html`
- **API Docs JSON** : `GET /v3/api-docs`

## 🗄️ Structure de Base de Données

### Table `customers`

| Colonne | Type | Contraintes | Description |
|---------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Identifiant unique |
| `nom` | VARCHAR(100) | NOT NULL | Nom du client |
| `email` | VARCHAR(150) | NOT NULL, UNIQUE | Email du client |
| `telephone` | VARCHAR(20) | - | Téléphone (optionnel) |
| `adresse` | VARCHAR(255) | - | Adresse (optionnelle) |
| `date_creation` | TIMESTAMP | NOT NULL | Date de création |
| `date_modification` | TIMESTAMP | NOT NULL | Date de modification |

### Index
- `idx_customers_email` sur `email`
- `idx_customers_nom` sur `nom`
- `idx_customers_date_creation` sur `date_creation`
- `idx_customers_adresse` sur `adresse`

## 📝 Exemples d'Utilisation

### Créer un Client
```bash
curl -X POST http://localhost:8081/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Jean Dupont",
    "email": "jean.dupont@email.com",
    "telephone": "0123456789",
    "adresse": "123 Rue de la Paix, Paris"
  }'
```

### Récupérer un Client
```bash
curl http://localhost:8081/api/customers/1
```

### Rechercher des Clients
```bash
# Par nom
curl "http://localhost:8081/api/customers/search/name?name=Jean"

# Multicritères avec pagination
curl "http://localhost:8081/api/customers/search?name=Jean&city=Paris&page=0&size=10"
```

## 🧪 Tests

### Exécuter les Tests
```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn integration-test

# Tous les tests
mvn verify
```

### Couverture de Code
```bash
mvn jacoco:report
# Rapport disponible dans target/site/jacoco/index.html
```

## 📈 Logs

Les logs sont configurés avec différents niveaux selon l'environnement :

- **Développement** : DEBUG pour le package customer, INFO pour le reste
- **Production** : WARN pour le système, INFO pour customer
- **Fichier de log** : `/var/log/customer-service.log` (production)

## 🔒 Sécurité

- Validation stricte des entrées
- Gestion des erreurs sans exposition d'informations sensibles
- Utilisateur non-root dans le conteneur Docker
- Health checks configurés

## 🐛 Dépannage

### Problèmes Courants

1. **Port déjà utilisé**
   - Changer le port via `SERVER_PORT=8082`

2. **Erreur de connexion DB**
   - Vérifier les paramètres de connexion
   - S'assurer que PostgreSQL est démarré

3. **Problème avec Eureka**
   - Vérifier que le serveur Eureka est accessible
   - Contrôler la configuration `eureka.client.service-url.defaultZone`

### Logs de Debug
```bash
# Activer les logs SQL
export SPRING_JPA_SHOW_SQL=true

# Logs détaillés
export LOGGING_LEVEL_COM_FLRXNT_CUSTOMER=DEBUG
```

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/nouvelle-fonctionnalite`)
3. Commit les changements (`git commit -am 'Ajout nouvelle fonctionnalité'`)
4. Push la branche (`git push origin feature/nouvelle-fonctionnalite`)
5. Créer une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 📞 Contact

- **Email** : support@flrxnt.com
- **Documentation** : [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Health Check** : [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)