# Utiliser OpenJDK 17 comme image de base
FROM openjdk:17-jdk-alpine

# Informations sur l'image
LABEL maintainer="Order Management System"
LABEL description="API Gateway pour Order Management System"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S gateway && \
    adduser -u 1001 -S gateway -G gateway

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR de l'application
COPY target/api-gateway-*.jar api-gateway.jar

# Changer le propriétaire du fichier
RUN chown gateway:gateway api-gateway.jar

# Utiliser l'utilisateur non-root
USER gateway

# Exposer le port 8080
EXPOSE 8080

# Variables d'environnement
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar api-gateway.jar"]

# Vérification de santé
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1