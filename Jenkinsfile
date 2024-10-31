pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPO_URI = '767397679048.dkr.ecr.us-east-1.amazonaws.com/hotelmanagement/autops'
        IMAGE_NAME = 'hotelmanagement/autops:latest'
        RESULTS_FILE = 'results.json'
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
                sh "trivy image --output ${RESULTS_FILE} --format json ${IMAGE_NAME}"
                    script {
                        def scanResults = readJSON file: "${RESULTS_FILE}"
                        def criticalVulnerabilities = scanResults.Vulnerabilities.findAll { it.Severity == 'CRITICAL' }
                        
                        if (criticalVulnerabilities.size() > 0) {
                            error "Scan found critical vulnerabilities: ${criticalVulnerabilities}"
                        }
                    }
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
