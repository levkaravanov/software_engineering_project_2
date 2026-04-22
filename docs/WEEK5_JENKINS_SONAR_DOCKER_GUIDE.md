# Week 5 Assignment Guide

This project is now prepared for the Week 5 Jenkins + SonarQube + Docker assignment and the Sprint 7 UAT submission.

## 1. Local Quality Baseline

Run:

```bash
mvn clean verify
```

Expected result:

- all tests pass
- JaCoCo check passes
- coverage is above the required 80%
- report is generated in `target/site/jacoco/index.html`

## 2. SonarQube / SonarCloud

Create `.env` from `.env.example` and fill your real values:

```bash
cp .env.example .env
set -a
source .env
set +a
```

Then run:

```bash
mvn sonar:sonar
```

Recommended verification:

- open the Sonar project
- confirm the new coverage value
- confirm the quality gate status

## 3. Jenkins Configuration

Create these Jenkins credentials:

1. `sonar-token`
   - Type: `Secret text`
   - Value: your Sonar token

2. `dockerhub`
   - Type: `Username with password`
   - Username: your Docker Hub username
   - Password: your Docker Hub access token

Configure the SonarQube server in Jenkins:

- Name: `SonarQubeServer`
- URL: your SonarQube or SonarCloud endpoint

Create a Pipeline job that points to this repository and uses the repository `Jenkinsfile`.

## 4. What the Pipeline Does

The pipeline now runs:

1. Checkout
2. `mvn clean verify`
3. Sonar analysis
4. Jenkins quality gate wait
5. `mvn -DskipTests package`
6. Docker image build
7. Docker smoke test
8. Docker tag + push to Docker Hub

The pipeline archives:

- `target/*.jar`
- `target/site/jacoco/**/*`
- Surefire XML reports

## 5. Docker Verification

Build manually if needed:

```bash
docker build -t YOUR_DOCKERHUB_USERNAME/shopping-cart:latest .
```

Smoke-test run:

```bash
docker run --rm YOUR_DOCKERHUB_USERNAME/shopping-cart:latest --smoke-test
```

Expected output:

```text
shopping-cart smoke test passed
```

If you want to run the JavaFX GUI from Docker, use an X server:

- Windows: Xming
- macOS: XQuartz

## 6. Docker Hub Push

After Jenkins succeeds, confirm:

- image with tag `${BUILD_NUMBER}` exists in Docker Hub
- `latest` tag exists in Docker Hub

## 7. Optional Minikube

If you want to do the optional part:

1. start Minikube
2. pull or build the image inside Minikube
3. create a simple Deployment with 1 replica
4. run the container with `--smoke-test` first for quick verification
5. if you want GUI testing, use a host/X11-capable setup separately

## 8. Sprint 7 UAT Artifact

Prepared workbook:

- `docs/Lev_Karavanov_Sprint7_UAT_Report.xlsx`

It includes:

- summary sheet
- 10 UAT test cases for the shopping cart application
- per-step pass/fail fields and comments

Before final submission, update any row if your manual execution result differs from the prepared draft.

## 9. Submission Checklist

Based on the assignment PDF, submit these:

1. Screenshot of the image running in Xming/X11 with your name visible
2. Screenshot of successful Jenkins execution
3. Screenshot of SonarQube / SonarCloud report
4. GitHub repository link
5. Individual UAT evaluation report to Oma
