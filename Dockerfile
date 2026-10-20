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
CMD ["java","-jar","app.jar"]