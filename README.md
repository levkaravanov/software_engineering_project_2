# Java Shopping Cart Application

Simple Java console shopping cart application with localization, tests, Docker, and Jenkins pipeline support.

## Requirements

- Java 17
- Maven 3.9+
- Docker

## Run locally

```bash
mvn clean package
java -jar target/shopping-cart-1.0.0.jar
```

## Run tests with coverage

```bash
mvn clean verify
```

JaCoCo HTML report will be generated at `target/site/jacoco/index.html`.

## Build Docker image

```bash
docker build -t your-dockerhub-username/shopping-cart:latest .
docker run -it your-dockerhub-username/shopping-cart:latest
```

## Jenkins setup

Create Jenkins credentials with this ID:

- `dockerhub` as a `Username with password` credential, where:
  - username = Docker Hub username
  - password = Docker Hub access token

Optional environment variable:

- `IMAGE_NAME` defaults to `shopping-cart`

## Submission checklist

- Push project to GitHub
- Build and push Docker image to Docker Hub
- Run the image in Play with Docker
- Capture a screenshot showing successful execution and your name or Docker Hub account
