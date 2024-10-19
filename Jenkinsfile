pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'  // Name of SonarQube instance in Jenkins config
        SONARQUBE_TOKEN = credentials('sonar') // Use credentials with ID 'sonar-token'
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
                // Run the Gradle build and test tasks
                script {
                    sh './gradlew clean build test'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Run SonarQube analysis
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh './gradlew sonarqube -Dsonar.login=$SONARQUBE_TOKEN'
                    }
                }
            }
        }

        stage('Quality Gate') {
            // This stage will pause the pipeline until SonarQube returns the quality gate result
            steps {
                timeout(time: 3, unit: 'MINUTES') { // Increased timeout to 3 minutes
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            // Archive the build results (e.g., JUnit test reports)
            junit 'build/test-results/test/*.xml'
        }

        failure {
            // Notify of failure, or handle failed builds
            echo "Build failed"
        }

        success {
            echo "Build succeeded!"
        }
    }
}
