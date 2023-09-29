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
      }
    }

  }
}