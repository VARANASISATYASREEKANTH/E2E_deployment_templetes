pipeline {
    agent any
    environment {
        VENV_DIR = 'venv'  // Virtual environment directory
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Setup Python Environment') {
            steps {
                sh '''
                python3 -m venv ${VENV_DIR}
                . ${VENV_DIR}/bin/activate
                pip install --upgrade pip
                pip install -r requirements.txt
                '''
            }
        }
        stage('Lint Code') {
            steps {
                sh '''
                . ${VENV_DIR}/bin/activate
                flake8 .
                '''
            }
        }
        stage('Run Unit Tests') {
            steps {
                sh '''
                . ${VENV_DIR}/bin/activate
                pytest --junitxml=test-results.xml
                '''
            }
        }
    }
    post {
        always {
            junit 'test-results.xml' // Publish test results
        }
        failure {
            echo 'Build failed!'
        }
        success {
            echo 'Build succeeded!'
        }
    }
}
