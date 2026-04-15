pipeline {
    agent any

    environment {
        IMAGE_NAME = "${env.IMAGE_NAME ?: 'shopping-cart'}"
        SONAR_HOST_URL = "${env.SONAR_HOST_URL ?: 'https://sonarcloud.io'}"
        SONAR_PROJECT_KEY = "${env.SONAR_PROJECT_KEY ?: 'levkaravanov_software_engineering_project_2'}"
        SONAR_ORGANIZATION = "${env.SONAR_ORGANIZATION ?: 'levkaravanov'}"
        SONAR_TOKEN_CREDENTIALS_ID = "${env.SONAR_TOKEN_CREDENTIALS_ID ?: 'sonar-token'}"
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
                withCredentials([string(credentialsId: "${SONAR_TOKEN_CREDENTIALS_ID}", variable: 'SONAR_TOKEN')]) {
                    sh 'mvn sonar:sonar'
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
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    sh 'docker build -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${BUILD_NUMBER} -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest .'
                }
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    sh 'echo ${DOCKERHUB_TOKEN} | docker login -u ${DOCKERHUB_USERNAME} --password-stdin'
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${BUILD_NUMBER}'
                    sh 'docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            junit 'target/surefire-reports/*.xml'
        }
    }
}
