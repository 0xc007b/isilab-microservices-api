# Dockerfile pour le service Order Management
# Utilise une approche multi-stage pour optimiser la taille de l'image

# Stage 1: Build stage
FROM maven:3.9.6-openjdk-17-slim AS build

# Informations sur l'image
LABEL maintainer="Order Management Team <backend@orderms.com>"
LABEL description="Order Management Service - Microservice pour la gestion des commandes"
LABEL version="1.0.0"

# Définition du répertoire de travail
WORKDIR /app

# Copie des fichiers Maven pour le cache des dépendances
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Téléchargement des dépendances (mise en cache)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copie du code source
COPY src src

# Construction de l'application (skip tests pour le build Docker)
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime stage
FROM openjdk:17-jre-slim

# Installation des outils nécessaires et nettoyage
RUN apt-get update && apt-get install -y \
    curl \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Création d'un utilisateur non-root pour la sécurité
RUN groupadd -r orderservice && useradd -r -g orderservice -u 1001 orderservice

# Définition du répertoire de travail
WORKDIR /app

# Création des répertoires nécessaires
RUN mkdir -p /app/logs && chown -R orderservice:orderservice /app

# Copie du JAR depuis le stage de build
COPY --from=build /app/target/*.jar app.jar

# Attribution des permissions
RUN chown orderservice:orderservice app.jar

# Passage à l'utilisateur non-root
USER orderservice

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=docker
ENV JVM_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"
ENV JAVA_OPTS="-Dspring.profiles.active=docker -Dfile.encoding=UTF-8 -Duser.timezone=UTC"

# Exposition du port
EXPOSE 8083

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8083/actuator/health || exit 1

# Point d'entrée avec configuration JVM optimisée
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS $JAVA_OPTS -jar app.jar"]
