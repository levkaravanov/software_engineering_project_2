FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -Djavafx.platform=linux -DskipTests package

FROM bellsoft/liberica-openjdk-debian:17

WORKDIR /app

COPY --from=build /workspace/target/shopping-cart-1.0.0.jar /app/app.jar
COPY --from=build /workspace/target/dependency /app/lib

ENTRYPOINT ["java", "-cp", "/app/app.jar:/app/lib/*", "com.example.shoppingcart.Launcher"]
