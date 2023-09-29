pipeline {
  agent {
    node {
      label 'dev-server'
    }

  }
  environment {
    EXCAMPLE_CREDS = credentials('c1a5809d-b070-4e45-a125-542e95e6d214')
  }
  stages {
    stage('pwd') {
      steps {
        sh 'pwd && hostname'
        sh 'echo hello world EXCAMPLE_CREDS_NAME'
      }
    }

  }
}
