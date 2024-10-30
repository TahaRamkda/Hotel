pipeline {
    agent any

    environment {
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo 'Cloning the repository...'
                git branch: 'main', url: 'https://github.com/TahaRamkda/Hotel.git'
            }
        }
        stage('Gradle Build and Test') {
            steps {
                script {
                    sh './gradlew clean build test'
                }
            }
        }

    post {
        always {
            junit 'build/test-results/test/*.xml'
        }

        failure {
            echo "Build failed"
        }

        success {
            echo "Build succeeded!"
        }
    }
}
}
