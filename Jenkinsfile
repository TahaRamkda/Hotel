pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPO_URI = '767397679048.dkr.ecr.us-east-1.amazonaws.com/hotelmanagement/autops'
        IMAGE_NAME = 'hotelmanagement/autops:latest'
    }

    stages {
        //01
        stage('Clone Repository') {
            steps {
                echo 'Cloning the repository...'
                git branch: 'main', url: 'https://github.com/TahaRamkda/Hotel.git'
            }
        }
        //02
        stage('Gradle Build and Test') {
            steps {
                script {
                    sh './gradlew clean build test'
                }
            }
        }
        //03
        stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t $IMAGE_NAME .'
                }
            }
        }
        //04
        stage('Trivy Security Scan') {
            steps {
            script {
                sh '''
                        docker run --rm \
                          -v /var/run/docker.sock:/var/run/docker.sock \
                          aquasec/trivy:latest image \
                          --cache-dir /path/to/cache --db-repository ghcr.io/aquasecurity/trivy-db:2 \
                          --severity HIGH,CRITICAL \
                          --output results.json \
                          --format json \
                          ${IMAGE_NAME}
                    '''
                }
            }
        }
        //05
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
    }

    post {
        always {
            junit 'build/test-results/test/*.xml'
        }
    }
}
