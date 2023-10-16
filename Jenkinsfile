pipeline {
  agent {
    node {
      label 'pre-prod-node'
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
        git(url: 'https://github.com/Kutuz0v/duty-helper/', branch: 'testing', credentialsId: 'GitHub (Kutuz0v)')
      }
    }

    stage('Building') {
      steps {
        sh '''docker build -t api .'''
      }
    }
    
//sh 'docker run --env-file ${EXCAMPLE_CREDS} --name test-env-file -d nginx'
    
    stage('Deploying') {
      steps {
          sh '''echo $ENV_FILE'''
          sh '''echo $ENV_FILE > myFile'''
        sh 'docker stop api'
        sh 'docker rm api'
        sh '''docker run \
--name api \
--network duty-helper \
-p 5000:5000 \
--env-file $ENV_FILE \
-v $ENV_FILE:$ENV_FILE:ro \
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
