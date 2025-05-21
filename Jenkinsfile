pipeline {
    agent any

    tools {
        maven 'M3' 
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
            environment {
                SONAR_TOKEN = credentials('sonar-token')
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                sh './mvnw sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'

                }
            }
        }
    }
    triggers {
    githubPush()
    }

    post {
        success {
            mail to: 'juans.garciaa@uqvirtual.edu.co',
                 subject: " Build SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "El build fue exitoso.\n\nRevisa en: ${env.BUILD_URL}"
        }
        failure {
            mail to: 'juans.garciaa@uqvirtual.edu.co',
                 subject: " Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "El build falló.\n\nRevisa en: ${env.BUILD_URL}"
        }
    }
}
