pipeline {
  agent {
    node {
      label 'dev-serv'
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