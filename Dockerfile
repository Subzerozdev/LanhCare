# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests)
RUN mvn clean package -DskipTests -B

# Runtime stage - use jammy variant
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy jar from build stage  
COPY --from=build /app/target/*.jar app.jar

# Set timezone
ENV TZ=Asia/Ho_Chi_Minh

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
