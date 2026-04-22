pipeline {
    agent any

    environment {
        IMAGE_NAME = "${env.IMAGE_NAME ?: 'shopping-cart'}"
        SONAR_HOST_URL = "${env.SONAR_HOST_URL ?: 'https://sonarcloud.io'}"
        SONAR_PROJECT_KEY = "${env.SONAR_PROJECT_KEY ?: 'levkaravanov_software_engineering_project_2'}"
        SONAR_ORGANIZATION = "${env.SONAR_ORGANIZATION ?: 'levkaravanov'}"
        SONARQUBE_ENV = "${env.SONARQUBE_ENV ?: 'SonarQubeServer'}"
        SONAR_TOKEN_CREDENTIALS_ID = "${env.SONAR_TOKEN_CREDENTIALS_ID ?: 'sonar-token'}"
        DOCKERHUB_CREDENTIALS_ID = "${env.DOCKERHUB_CREDENTIALS_ID ?: 'dockerhub'}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    withCredentials([string(credentialsId: "${SONAR_TOKEN_CREDENTIALS_ID}", variable: 'SONAR_TOKEN')]) {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -DskipTests package'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} -t ${IMAGE_NAME}:latest .'
            }
        }

        stage('Docker Smoke Test') {
            steps {
                sh 'docker run --rm ${IMAGE_NAME}:${BUILD_NUMBER} --smoke-test'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS_ID}", usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    sh 'docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${BUILD_NUMBER}'
                    sh 'docker tag ${IMAGE_NAME}:latest ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest'
                    sh 'echo ${DOCKERHUB_TOKEN} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin'
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${BUILD_NUMBER}'
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar,target/site/jacoco/**/*', fingerprint: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}
