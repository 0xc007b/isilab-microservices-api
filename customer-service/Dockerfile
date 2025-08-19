# Dockerfile pour le service Customer
# Utilise une approche multi-stage pour optimiser la taille de l'image

# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .

# Télécharger les dépendances (optimisation du cache Docker)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src/ src/

# Construire l'application
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-jammy

# Créer un utilisateur non-root pour la sécurité
RUN groupadd -r customerservice && useradd -r -g customerservice customerservice

# Définir le répertoire de travail
WORKDIR /app

# Installer des outils utiles pour le debugging (optionnel)
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/*

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/customer-service-*.jar customer-service.jar

# Créer le répertoire pour les logs
RUN mkdir -p /var/log && \
    chown -R customerservice:customerservice /var/log /app

# Changer vers l'utilisateur non-root
USER customerservice

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8081
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Exposer le port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Point d'entrée avec configuration JVM optimisée
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    -Dserver.port=${SERVER_PORT} \
    -jar customer-service.jar"]

# Métadonnées
LABEL maintainer="flrxnt@example.com"
LABEL version="1.0.0"
LABEL description="Customer Service - Microservice de gestion des clients"
LABEL service="customer-service"
