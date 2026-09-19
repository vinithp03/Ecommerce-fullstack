pipeline {
    agent {
        label 'mac'
    }

    stages {

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
                sh '''
                    cd /Users/vinithpoojary/Desktop/ecommerce-fullstack
                    docker compose up --no-deps -d catalog-service
                '''
            }
        }
    }
}