pipeline {
    agent {
        label 'mac'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Catalog') {
            steps {
                dir('backend/catalog') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker compose build --no-cache catalog-service'
            }
        }

        stage('Deploy Catalog') {
            steps {
                sh 'docker compose up catalog-service -d'
            }
        }
    }
}