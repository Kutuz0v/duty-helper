pipeline {
  agent {
    node {
      label 'prod-node'
    }
  }
  
  environment {
        TOKEN = credentials('jenkins-reporter-telegram-token')
        CHAT_ID = credentials('m.hurin-telegram.chatId')
        ENV_FILE = credentials('prod-env-file')
  }
    
  stages {
    stage('Clean Workspace') {
      steps {
        deleteDir()
      }
    }

    stage('Checkout Code') {
      steps {
        git(url: 'https://github.com/Kutuz0v/duty-helper/', branch: 'master', credentialsId: 'GitHub (Kutuz0v)')
      }
    }

    stage('Building') {
      steps {
        sh '''docker build -t api .'''
      }
    }

    stage('Deploying') {
      steps {
        sh 'docker stop api'
        sh 'docker rm api'
        sh '''docker run \
--name api \
--network duty-helper \
-p 5000:5000 \
--env-file $ENV_FILE
-v /etc/timezone:/etc/timezone:ro \
-v /etc/localtime:/etc/localtime:ro \
-v /var/log:/var/log \
--restart=always \
-d \
api'''
      }
    }

  }
  
  post { 
        always {
            script {
                GIT_COMMIT_MSG = sh (script: 'git log -1 --pretty=%B ${GIT_COMMIT}', returnStdout: true).trim()
                env.MESSAGE = """Building of ${currentBuild.fullDisplayName} ended on node ${NODE_NAME} with result: \n${currentBuild.result} in ${currentBuild.duration.intdiv(1000)} sec.\nCommit message: $GIT_COMMIT_MSG"""
            }
            sh '''curl -s -X POST https://api.telegram.org/bot$TOKEN/sendMessage -d chat_id=$CHAT_ID -d text="$MESSAGE" > /dev/null'''
        }
    }
  
}
