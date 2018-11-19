node{
	def server = Artifactory.server 'ART'
   	def rtMaven = Artifactory.newMavenBuild()
	def mvnHome = tool name: 'M2_HOME', type: 'maven'
   	def buildInfo
	
  stage('Git Checkout'){
	git branch: 'DEV', credentialsId: '4c9741a2-4c15-4aad-a13d-39f4302a0e77', url: 'git@github.com:mosip/mosip.git'
  }
  stage('MVN Package'){    
    sh "${mvnHome}/bin/mvn clean package"
  } 
  stage ('Artifactory configuration') {
	rtMaven.tool = 'M2_HOME'
        rtMaven.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: server
        rtMaven.resolver releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server
        buildInfo = Artifactory.newBuildInfo()
	buildInfo.env.capture = true 
   }
  stage ('Packaging') 
	{
        rtMaven.run pom: 'DEV/kernel/pom.xml', goals: 'clean install sonar:sonar', buildInfo: buildInfo
    }
	
  stage ('Publish build info') {
        server.publishBuildInfo buildInfo
    }
