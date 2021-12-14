pipeline {
    agent any
    tools {
        maven 'MAVEN'
        jdk 'JAVA'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                echo "Building..."
                sh 'mvn clean install -DskipTests'
            }

        }

        stage ('Test') {
             steps {
                 echo "Testing..."
                 sh 'mvn clean install'
             }
        }

        stage ('Deploy') {
            steps {
                script {
                    docker.withRegistry (
                        '058587610590.dkr.ecr.eu-west-1.amazonaws.com/atm',
                        'ecr:eu-west-1:AWS') {
                        def myImage = docker.build('atm-machine')
                        myImage.push('atm-machine')
                    }
                }
            }
        }
    }
}