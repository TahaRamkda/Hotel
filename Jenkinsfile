pipeline {
    agent any

    tools {
        gradle 'gradle_8.11'
    }

    environment {
        GITHUB_REPO = 'https://github.com/TahaRamkda/Calculator.git'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'master', url: "${env.GITHUB_REPO}"
            }
        }

        stage('Build and Test') {
            steps {
                script {
                    sh './gradlew clean build'
                }
            }
        }

        stage('Test Results') {
            steps {
                junit '**/build/test-results/test/*.xml'
            }
        }
    }

    post {
        success {
            echo 'Build and test successful!'
        }
        failure {
            echo 'Build or tests failed.'
        }
    }
}
