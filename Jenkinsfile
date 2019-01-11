node {
 def server = Artifactory.server 'ART'
 def rtMaven = Artifactory.newMavenBuild()
 def buildInfo
 def branch = 'DEV'

 stage('------- Checkout --------') {
  // Checkout all the modules
  dir(branch) {
   checkout([$class: 'GitSCM',
    branches: [
     [name: branch]
    ],
    userRemoteConfigs: [
     [url: 'https://github.com/mosip/mosip', credentialsId: '4c9741a2-4c15-4aad-a13d-3a1206u05321']
    ]
   ])
  }
 }

 stage('--------- Artifactory configuration ----------------') {
  rtMaven.tool = 'M2_HOME' // Tool name from Jenkins configuration
  rtMaven.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: server
  rtMaven.resolver releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server
  buildInfo = Artifactory.newBuildInfo()
  buildInfo.env.capture = true
 }

 stage('---------- mvn-clean-install ---------------') {
  rtMaven.run pom: branch + '/pom.xml', goals: 'clean install', buildInfo: buildInfo
 }

 stage('---------- SonarQube Analysis --------------') {
  def mvnHome = tool name: 'M2_HOME', type: 'maven'
  withSonarQubeEnv('sonar') {
   sh "${mvnHome}/bin/mvn -f '$branch/' sonar:sonar"
  }
 }

 stage('----------- Publish build info -------------') {
  server.publishBuildInfo buildInfo
 }
}
