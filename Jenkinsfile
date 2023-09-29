pipeline {
  agent {
    node {
      label 'dev-server'
    }

  }
  stages {
    stage('pwd') {
      steps {
        sh 'pwd && hostname'
        sh 'echo "hello world ${EXCAMPLE_CREDS}"'
      }
    }

  }
  environment {
    EXCAMPLE_CREDS = credentials('DB_PASSWORD')
  }
}
