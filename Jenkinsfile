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
        stage('Prepare Trivy Cache') {
            steps {
                script {
                    sh 'mkdir -p /var/cache/trivy'
                }
            }
        }

        stage('Download Trivy DB') {
            steps {
                    withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws-credentials')]) {
                    script {
                    try {
                        echo 'Downloading Trivy DB...'
                        sh 'aws s3 cp s3://cachefortrivy/trivy/trivy.db /var/cache/trivy/trivy.db'
                    } catch (Exception e) {
                        echo 'Failed to download Trivy DB from S3. Proceeding to pull from Trivy default...'
                    }

                    // Attempt to pull the Trivy DB if the previous step failed
                    sh 'trivy db pull || echo "Failed to pull Trivy DB, using local cache."' 
                    }
                }
            }
        }

        stage('Trivy Security Scan') {
            steps {
                script {
                    try {
                        sh 'trivy image --quiet --severity CRITICAL,HIGH --cache-dir /var/cache/trivy --format json -o results.json hotelmanagement/autops:latest'
                    } catch (Exception e) {
                        echo 'Trivy scan failed. Please check results.json for issues.'
                    }
                }
            }
        }
        //07
        stage('Push to ECR') {
            steps {
                withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY', credentialsId: 'aws-credentials')]) {
                    script {
                        // Log in to ECR
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
            archiveArtifacts artifacts: '**/results.json', allowEmptyArchive: true
            echo 'Finished: ${currentBuild.currentResult}'
        }
    }
}

