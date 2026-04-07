# Lev Karavanov / JavaFX Shopping Cart App

JavaFX GUI shopping cart application with multi-language localization (English, Finnish, Swedish, Japanese, Arabic), JUnit 5 unit tests, JaCoCo coverage, Docker, and Jenkins CI/CD pipeline.

## Features

- Select language from a dropdown — UI updates dynamically
- Enter number of items, then price and quantity for each
- Calculates total per item and overall cart total
- Arabic locale renders right-to-left (RTL)
- Localization via `.properties` resource bundles (UTF-8)

## Supported Languages

| Language | Locale | File |
|---|---|---|
| English (default) | en_US | `MessagesBundle_en_US.properties` |
| Finnish | fi_FI | `MessagesBundle_fi_FI.properties` |
| Swedish | sv_SE | `MessagesBundle_sv_SE.properties` |
| Japanese | ja_JP | `MessagesBundle_ja_JP.properties` |
| Arabic | ar_AR | `MessagesBundle_ar_AR.properties` |

## Requirements

- Java 17
- Maven 3.9+
- Docker

## Run locally

```bash
mvn clean javafx:run
```

## Run tests with coverage

```bash
mvn clean verify
```

JaCoCo HTML report: `target/site/jacoco/index.html`

## Build Docker image

```bash
docker build -t your-dockerhub-username/shopping-cart:latest .
docker run -it your-dockerhub-username/shopping-cart:latest
```

## Jenkins CI/CD

Create a `dockerhub` credential (`Username with password`) in Jenkins:
- Username: Docker Hub username
- Password: Docker Hub access token

Optional env variable: `IMAGE_NAME` (defaults to `shopping-cart`)

## Submission Checklist

- [x] JavaFX GUI with localization (5 languages)
- [x] RTL support for Arabic
- [x] JUnit 5 unit tests
- [x] JaCoCo test coverage
- [x] Dockerfile
- [x] Jenkinsfile (CI/CD pipeline)
- [ ] Push project to GitHub
- [ ] Build and push Docker image to Docker Hub
- [ ] Screenshots of UI (all 5 languages, name visible in window title)
- [ ] Submit in Oma
