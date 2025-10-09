# Use an OpenJDK image with Maven to build, then create a slim runtime image
# Multi-stage build

# Build stage
FROM maven:3.9.6 AS build
WORKDIR /app

# Copy pom and download dependencies (leverages docker cache)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN mvn -B -ntp dependency:go-offline

# Copy sources & build
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime stage
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

# create non-root user on Alpine
RUN addgroup -S appuser && adduser -S -G appuser appuser

# copy jar from build stage (as root), then chown it for the appuser
COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appuser /app/app.jar

# switch to non-root user
USER appuser

# Expose port (match spring.server.port)
EXPOSE 8080

# Provide a healthcheck (optional)
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Default command
ENTRYPOINT ["java","-jar","/app/app.jar"]
