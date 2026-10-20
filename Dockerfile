FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Install standard Maven via apt-get so you don't rely on mvnw wrapper files
RUN apt-get update && apt-get install -y maven

COPY . ./
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080

# Updated to include your required system properties (switched dev to prod)
CMD ["sh", "-c", "java -Duser.timezone=Asia/Kolkata -Dspring.profiles.active=prod -jar app.jar"]
