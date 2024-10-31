pipeline {
    agent any

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
    }

    stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t $IMAGE_NAME .'
                }
            }
        }
        
        stage('Push to ECR') {
            steps {
                withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws_credentials')]) {
                    script {
                        // Authenticate with ECR
                        sh 'aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REPO_URI'
                        
                        // Tag and push the image
                        sh 'docker tag $IMAGE_NAME $ECR_REPO_URI:latest'
                        sh 'docker push $ECR_REPO_URI:latest'
                    }
                }
            }
        }

    post {
        always {
            junit 'build/test-results/test/*.xml'
        }
    }
}
