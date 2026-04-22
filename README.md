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
set -a
source .env
set +a
mvn clean javafx:run
```

## Run tests with coverage

```bash
mvn clean verify
```

JaCoCo HTML report: `target/site/jacoco/index.html`

Current local baseline after the Week 5 extension:
- `mvn clean verify` passes
- JaCoCo line coverage is above the required 80% threshold

## SonarCloud

Create `.env` in the project root using `.env.example` as a template:

```bash
cp .env.example .env
```

Fill in `SONAR_TOKEN` in `.env`, then export the variables into the current shell and run analysis:

```bash
set -a
source .env
set +a
mvn clean verify sonar:sonar
```

Project key: `levkaravanov_software_engineering_project_2`
Organization: `levkaravanov`
Host: `https://sonarcloud.io`

## Database configuration

The application reads the database connection from environment variables or Java system properties:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

The values can be stored in `.env` and exported before running the app or tests:

```bash
cp .env.example .env
set -a
source .env
set +a
```

## Build Docker image

```bash
docker build -t your-dockerhub-username/shopping-cart:latest .
docker run --rm your-dockerhub-username/shopping-cart:latest --smoke-test
```

The Docker build is multi-stage: it packages the application and copies all runtime dependencies inside the Linux image, so the container does not depend on host-built Maven artifacts.

The container includes a smoke-test mode for CI/CD verification. It validates that the packaged application resources are present and exits with code `0` when the image is healthy.

If you want to run the JavaFX GUI itself from Docker, you need an X server on the host machine. For example, on Windows this can be Xming; on macOS, XQuartz.

## Jenkins CI/CD

Create a `dockerhub` credential (`Username with password`) in Jenkins:
- Username: Docker Hub username
- Password: Docker Hub access token

Create a `sonar-token` credential (`Secret text`) in Jenkins:
- Secret: SonarQube or SonarCloud token

Configure the SonarQube server in Jenkins as `SonarQubeServer` or override the name with `SONARQUBE_ENV`.

The pipeline now performs:
- `mvn clean verify`
- Sonar analysis
- Jenkins quality gate wait
- JAR packaging
- Docker image build
- Docker smoke test using `--smoke-test`
- Docker push to Docker Hub

Optional env variable: `IMAGE_NAME` (defaults to `shopping-cart`)
