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

    stage('up fake container') {
      steps {
        sh 'docker run --env-file ${EXCAMPLE_CREDS} --name test-env-file -d nginx'
      }
    }

  }
  environment {
    EXCAMPLE_CREDS = credentials('dev-server-env')
  }
}
