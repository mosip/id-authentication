node{
	def server = Artifactory.server 'ART'
   	def rtMaven = Artifactory.newMavenBuild()
   	def buildInfo
	def branch = 'DEV'
	def projectToBuild = 'kernel'
   
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
  stage ('Packaging') 
	{
        rtMaven.run pom: 'DEV/kernel/pom.xml', goals: 'clean install sonar:sonar', buildInfo: buildInfo
    }
	
	stage ('Publish build info') {
        server.publishBuildInfo buildInfo
    }
}
