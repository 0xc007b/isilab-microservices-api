# Utiliser OpenJDK 17 comme image de base
FROM openjdk:17-jdk-alpine

# Informations sur l'image
LABEL maintainer="Order Management System"
LABEL description="Eureka Server pour la découverte de services"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S eureka && \
    adduser -u 1001 -S eureka -G eureka

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR de l'application
COPY target/eureka-server-*.jar eureka-server.jar

# Changer le propriétaire du fichier
RUN chown eureka:eureka eureka-server.jar

# Utiliser l'utilisateur non-root
USER eureka

# Exposer le port 8761
EXPOSE 8761

# Variables d'environnement
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar eureka-server.jar"]

# Vérification de santé
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8761/actuator/health || exit 1