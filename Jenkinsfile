pipeline {
    agent any

    tools {
        maven 'M3' // Nombre del Maven configurado en Jenkins
    }

    environment {
        SONAR_HOST_URL = 'http://sonar:9000'
        SONAR_SCANNER_OPTS = '-Dsonar.projectKey=JavaClassroom'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/JuanSebastianGarcia/JavaClassroom.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh './mvnw clean verify'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './mvnw sonar:sonar'
                }
            }
        }
    }
}
