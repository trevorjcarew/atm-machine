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
    }
}