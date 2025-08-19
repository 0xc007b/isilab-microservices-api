# Guide de Développement - Customer Service

## 🔧 Configuration de l'Environnement de Développement

### Prérequis
- Java 17 ou supérieur
- Maven 3.8+
- IDE (IntelliJ IDEA, Eclipse, VS Code)
- Git
- Docker Desktop (optionnel)
- PostgreSQL 15+ (optionnel, pour tests avec vraie DB)

### Configuration IDE

#### IntelliJ IDEA
1. Importer le projet Maven
2. Configurer le SDK Java 17
3. Activer les annotations processors
4. Installer les plugins :
   - Spring Boot
   - Docker
   - Database Tools

#### Eclipse
1. Importer comme projet Maven existant
2. Configurer le JRE 17
3. Installer Spring Tools Suite

#### VS Code
Extensions recommandées :
- Extension Pack for Java
- Spring Boot Extension Pack
- Docker
- PostgreSQL

### Configuration Locale

#### 1. Variables d'environnement
Créer un fichier `.env` local (ignoré par Git) :
```bash
# Base de données
DATABASE_URL=jdbc:postgresql://localhost:5432/customer_db
DATABASE_USERNAME=customer_user
DATABASE_PASSWORD=customer_password

# Eureka
EUREKA_URL=http://localhost:8761/eureka/

# Profil
SPRING_PROFILES_ACTIVE=dev

# JVM
JAVA_OPTS=-Xmx512m -Xms256m -Dspring.profiles.active=dev
```

#### 2. Configuration Base de Données PostgreSQL (Optionnel)
```sql
-- Se connecter à PostgreSQL
psql -U postgres

-- Créer la base de données et l'utilisateur
CREATE DATABASE customer_db;
CREATE USER customer_user WITH PASSWORD 'customer_password';
GRANT ALL PRIVILEGES ON DATABASE customer_db TO customer_user;

-- Donner les permissions sur le schéma
\c customer_db
GRANT ALL ON SCHEMA public TO customer_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO customer_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO customer_user;
```

#### 3. Configuration H2 (Développement)
Le profil `dev` utilise H2 par défaut :
- Console H2 : http://localhost:8081/h2-console
- JDBC URL : `jdbc:h2:mem:customerdb`
- Username : `sa`
- Password : (vide)

## 🚀 Lancement en Mode Développement

### Méthode 1 : Maven
```bash
# Démarrage rapide avec H2
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Avec PostgreSQL
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Méthode 2 : IDE
1. Créer une configuration de lancement
2. Classe principale : `CustomerServiceApplication`
3. VM options : `-Dspring.profiles.active=dev`
4. Programme arguments : `--server.port=8081`

### Méthode 3 : Docker Compose (Pile complète)
```yaml
# docker-compose.dev.yml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: customer_db
      POSTGRES_USER: customer_user
      POSTGRES_PASSWORD: customer_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  customer-service:
    build: .
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:postgresql://postgres:5432/customer_db
    depends_on:
      - postgres

volumes:
  postgres_data:
```

## 🧪 Tests et Développement

### Structure des Tests
```
src/test/java/com/flrxnt/customer/
├── controller/          # Tests des contrôleurs
├── service/            # Tests des services
├── repository/         # Tests des repositories
├── integration/        # Tests d'intégration
└── utils/             # Utilitaires de test
```

### Lancement des Tests
```bash
# Tests unitaires uniquement
mvn test

# Tests d'intégration
mvn integration-test

# Tous les tests avec rapport de couverture
mvn clean verify jacoco:report
```

### Base de Données de Test
Les tests utilisent TestContainers pour PostgreSQL :
```java
@Testcontainers
class CustomerRepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
}
```

## 🔍 Debugging

### Logs de Développement
Configuration dans `application-dev.yml` :
```yaml
logging:
  level:
    com.flrxnt.customer: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type: TRACE
```

### Points de Debug Courants
1. **Contrôleurs** : Vérifier les paramètres de requête
2. **Services** : Logique métier et validations
3. **Repositories** : Requêtes SQL générées
4. **Mappers** : Conversions DTO ↔ Entity

### Outils de Debug
- **H2 Console** : http://localhost:8081/h2-console
- **Actuator Health** : http://localhost:8081/actuator/health
- **Swagger UI** : http://localhost:8081/swagger-ui.html
- **Logs** : `tail -f logs/customer-service.log`

## 📊 Monitoring Local

### Endpoints de Développement
- Health Check : `GET /actuator/health`
- Metrics : `GET /actuator/metrics`
- Environment : `GET /actuator/env`
- Beans : `GET /actuator/beans`

### Profiling
Pour profiler l'application :
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xprof -XX:+PrintGCDetails"
```

## 🛠️ Développement de Nouvelles Fonctionnalités

### Checklist pour Nouvelle Fonctionnalité
1. [ ] Créer une branche feature
2. [ ] Définir les DTOs si nécessaire
3. [ ] Implémenter la logique service
4. [ ] Ajouter les endpoints REST
5. [ ] Écrire les tests unitaires
6. [ ] Écrire les tests d'intégration
7. [ ] Mettre à jour la documentation
8. [ ] Tester manuellement avec Postman/curl

### Convention de Nommage
- **Branches** : `feature/nom-fonctionnalite`
- **Commits** : `feat: description de la fonctionnalité`
- **Classes** : PascalCase
- **Méthodes** : camelCase
- **Constants** : SCREAMING_SNAKE_CASE

### Pattern de Développement
```java
// 1. DTO
public class NewFeatureDTO { ... }

// 2. Entity (si nécessaire)
@Entity
public class NewFeature { ... }

// 3. Repository
public interface NewFeatureRepository extends JpaRepository<NewFeature, Long> { ... }

// 4. Service Interface
public interface NewFeatureService { ... }

// 5. Service Implementation
@Service
public class NewFeatureServiceImpl implements NewFeatureService { ... }

// 6. Controller
@RestController
public class NewFeatureController { ... }

// 7. Tests
@Test
void should... { ... }
```

## 🔄 Hot Reload

### Spring Boot DevTools
Ajouter dans `pom.xml` (mode développement) :
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### Configuration IDE pour Hot Reload
- **IntelliJ** : Build Project (Ctrl+F9) après modification
- **Eclipse** : Sauvegarde automatique avec recompilation
- **VS Code** : Extension Spring Boot Dashboard

## 📝 Documentation

### Swagger/OpenAPI
- URL locale : http://localhost:8081/swagger-ui.html
- JSON API Docs : http://localhost:8081/v3/api-docs

### Génération de Documentation
```bash
# Générer la documentation API
mvn compile

# Documentation JavaDoc
mvn javadoc:javadoc
```

## 🚨 Dépannage Courant

### Port déjà utilisé
```bash
# Trouver le processus utilisant le port 8081
netstat -ano | findstr :8081   # Windows
lsof -ti:8081                  # Linux/Mac

# Tuer le processus
taskkill /PID <PID> /F         # Windows
kill -9 <PID>                  # Linux/Mac
```

### Problèmes de Base de Données
```bash
# Réinitialiser H2
# Supprimer les fichiers *.db dans le répertoire home

# Réinitialiser PostgreSQL
DROP DATABASE customer_db;
CREATE DATABASE customer_db;
```

### Erreurs de Compilation
```bash
# Nettoyer et recompiler
mvn clean compile

# Réimporter les dépendances
mvn dependency:resolve

# Nettoyer le cache IDE
# IntelliJ: File > Invalidate Caches and Restart
```

## 🔒 Bonnes Pratiques

### Sécurité
- Ne jamais commiter les mots de passe
- Utiliser des variables d'environnement
- Valider toutes les entrées utilisateur

### Performance
- Utiliser la pagination pour les listes
- Indexer les colonnes de recherche
- Optimiser les requêtes N+1

### Code Quality
- Suivre les conventions Java
- Écrire des tests pour chaque nouvelle fonctionnalité
- Documenter les méthodes publiques
- Utiliser les logs appropriés

## 📞 Support

- **Documentation API** : http://localhost:8081/swagger-ui.html
- **Health Check** : http://localhost:8081/actuator/health
- **Logs** : `logs/customer-service.log`
- **Issues** : Repository GitHub