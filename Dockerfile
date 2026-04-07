FROM bellsoft/liberica-openjdk-debian:17

WORKDIR /app

COPY target/shopping-cart-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
