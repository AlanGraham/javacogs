pipeline {
    agent any
    stages {
        /*stage ('Checkout') {
            steps {
                checkout SCM
            }
        }*/
    
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    mvn clean
                '''
            }
        }
        
        stage ('Tests') {
            steps {
                sh 'mvn test' 
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn install' 
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'target/javacogs-*.jar', fingerprint: true
        }
    }
}