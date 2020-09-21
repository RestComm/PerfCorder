def build() {
        // Run the maven build with in-module unit testing
        sh "mvn -B -f pom.xml -P ${MVN_PROFILES} clean deploy"
}

def tag() {
    // Save release version
    def pom = readMavenPom file: 'pom.xml'
    releaseVersion = pom.version
    echo "Set release version to ${releaseVersion}"

    withCredentials([usernamePassword(credentialsId: 'c2cce724-a831-4ec8-82b1-73d28d1c367a', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
        sh('git fetch https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/RestComm/PerfCorder.git')
        sh("git commit -a -m \"New release candidate ${releaseVersion}\"")
        sh("git tag ${releaseVersion}")
        sh('git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/RestComm/PerfCorder.git --tags')
    }
}

def version() {
    def newVersion = "${MAJOR_VERSION_NUMBER}"
    if (BRANCH_NAME != "master") {
        newVersion = "${MAJOR_VERSION_NUMBER}-${BRANCH_NAME}"
    }
    currentBuild.displayName = "#${BUILD_NUMBER}-${newVersion}"
    sh "mvn -B versions:set -DnewVersion=${newVersion} versions:commit"

}

def isSnapshot() {
    return MAJOR_VERSION_NUMBER.contains("SNAPSHOT")
}

node("cxs-slave-master") {
    properties([[$class: 'DatadogJobProperty', tagFile: '', tagProperties: ''],
        parameters([
            string(defaultValue: '1.1.0-SNAPSHOT', description: 'Snapshots will skip Tag stage', name: 'MAJOR_VERSION_NUMBER', trim: false),
            string(defaultValue: 'regular', description: 'docs,release,docker', name: 'MVN_PROFILES', trim: false)
        ])
    ])

    if (isSnapshot()) {
        echo "SNAPSHOT detected, skip Tag stage"
    }

    configFileProvider(
        [configFile(fileId: 'c33123c7-0e84-4be5-a719-fc9417c13fa3',  targetLocation: 'settings.xml')]) {
	    sh 'mkdir -p ~/.m2 && sed -i "s|@LOCAL_REPO_PATH@|$WORKSPACE/M2_REPO|g" $WORKSPACE/settings.xml && cp $WORKSPACE/settings.xml -f ~/.m2/settings.xml'
    }

    stage ('Checkout') {
        checkout scm
    }

    // Define Java and Maven versions (named according to Jenkins installed tools)
    // Source: https://jenkins.io/blog/2017/02/07/declarative-maven-project/
    def mvnHome = tool name: 'Maven-3.5.0'

    // Set JAVA_HOME, and special PATH variables.
    List javaEnv = [
            "PATH+MVN=${mvnHome}/bin",
            "M2_HOME=${mvnHome}"
    ]

    withEnv(javaEnv) {

        stage('Versioning') {
            version()
        }

        stage ("Build") {
            build()
        }

        if ( !isSnapshot()) {
            stage('Tag') {
                tag()
            }
        }
    }
}
