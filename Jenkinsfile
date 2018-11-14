node{
	def server = Artifactory.server 'ART'
   	def rtMaven = Artifactory.newMavenBuild()
   	def buildInfo
	def mvnHome =  tool name: 'M2_HOME', type: 'maven'
	def branch = 'DEV'
	def projectToBuild = 'kernel'
	def registry = 'codeguna/mosip'
	
	def registryCredential = '0b561449-5504-42bf-bbb9-df38f2a2909a'
   
	stage ('SCM') {
	dir(branch) { checkout([$class: 'GitSCM',
    branches: [[name: branch]],
    userRemoteConfigs: [[url: 'https://github.com/mosip/mosip', credentialsId: 'c31410c5-ffb5-47de-a5b8-941984cead45']],
    extensions: [
     [$class: 'PathRestriction', excludedRegions: '', includedRegions: projectToBuild +'/.*'],
     [$class: 'SparseCheckoutPaths', sparseCheckoutPaths: [
      [$class: 'SparseCheckoutPath', path: projectToBuild + '/']
     ]]],])}    
	}
	
   stage ('Artifactory configuration') {
	rtMaven.tool = 'M2_HOME'
        rtMaven.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: server
        rtMaven.resolver releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server
        buildInfo = Artifactory.newBuildInfo()
	buildInfo.env.capture = true 
   }
  stage ('Maven Compile') 
	{
        rtMaven.run pom: 'DEV/kernel/pom.xml', goals: 'clean install -Dmaven.test.skip=true', buildInfo: buildInfo
    }	
  stage('SonarQube Analysis') {
	withSonarQubeEnv('sonar') { 
          rtMaven.run pom: 'DEV/kernel/pom.xml', goals: 'sonar:sonar', buildInfo: buildInfo
        }	}
  		
	stage ('Publish') {
        server.publishBuildInfo buildInfo
    }
	 stage('Build image') {
	 dir(branch) {
	 docker.withRegistry('https://registry.hub.docker.com', registryCredential) {
     def buildName = registry + ":kernel-auditmanager-service-$BUILD_NUMBER"
     newApp = docker.build(buildName, '-f /kernel/kernel-auditmanager-service/Dockerfile kernel/kernel-auditmanager-service/')
     newApp.push()
   }
  }
 }
 
 stage('Register image') {

  docker.withRegistry('https://registry.hub.docker.com', registryCredential) {
   newApp.push 'kernel-auditmanager-service-latest'
  }
 }
 stage('Remove image from local') {
  sh "docker rmi $registry:kernel-auditmanager-service-$BUILD_NUMBER"
  sh "docker rmi registry.hub.docker.com/$registry:kernel-auditmanager-service-$BUILD_NUMBER"
 }
}
