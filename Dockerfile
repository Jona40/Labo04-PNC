# ETAPA 1: Construcción
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear usuario no root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el jar desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de tu app (normalmente 8080)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]