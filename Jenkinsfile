pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPO_URI = '767397679048.dkr.ecr.us-east-1.amazonaws.com/hotelmanagement/autops'
        IMAGE_NAME = 'hotelmanagement/autops:latest'
        TRIVY_CACHE_DIR = '/var/cache/trivy'
        S3_BUCKET = 'cachefortrivy'
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
        withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws-credentials')]) {
            script {
                // Download Trivy DB from S3
                sh "aws s3 cp s3://${S3_BUCKET}/trivy/trivy.db ${TRIVY_CACHE_DIR}/trivy.db || true"

                // Run Trivy scan
                def scanResult = sh(script: "trivy image --quiet --severity CRITICAL,HIGH --cache-dir ${TRIVY_CACHE_DIR} --format json -o results.json ${IMAGE_NAME}", returnStatus: true)
                
                if (scanResult != 0) {
                    def scanResults = readJSON file: 'results.json'
                    def criticalOrHighVulnerabilities = scanResults.Vulnerabilities.findAll { it.Severity in ['CRITICAL', 'HIGH'] }
                    if (criticalOrHighVulnerabilities.size() > 0) {
                        echo "Critical or high vulnerabilities found: ${criticalOrHighVulnerabilities}"
                        error "Scan found critical/high vulnerabilities: ${criticalOrHighVulnerabilities}"
                    } else {
                        echo "No critical or high vulnerabilities found."
                    }
                }
                
                // Update Trivy DB in S3 for future use
                sh "aws s3 cp ${TRIVY_CACHE_DIR}/trivy.db s3://${S3_BUCKET}/trivy/trivy.db"
            }
        }
    }
}

        //05
        stage('Push to ECR') {
            steps {
                withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws_credentials')]) {
                    script {
                        sh 'aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REPO_URI'
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