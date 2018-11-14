node{
	def server = Artifactory.server 'ART'
    def rtMaven = Artifactory.newMavenBuild()
    def buildInfo
   
	stage ('SCM') {
        git branch: 'DEV', credentialsId: '4c9741a2-4c15-4aad-a13d-39f4302a0e77', url: 'git@github.com:mosip/mosip.git'
    }
	
	stage('Compile-Package'){
      // Get maven home path
      def mvnHome =  tool name: 'M2_HOME', type: 'maven'   
      sh "${mvnHome}/bin/mvn compile"
   } 
   
   stage('SonarQube Analysis') {
        def mvnHome =  tool name: 'M2_HOME', type: 'maven'
        withSonarQubeEnv('sonar') { 
          sh "${mvnHome}/bin/mvn sonar:sonar"
        }
    }
	
   stage ('Artifactory configuration') {
		rtMaven.tool = 'M2_HOME' // Tool name from Jenkins configuration
        rtMaven.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: server
        rtMaven.resolver releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server
        buildInfo = Artifactory.newBuildInfo()
		buildInfo.env.capture = true
		}
		 stage ('Exec Maven') {
        rtMaven.run pom: 'pom.xml', goals: 'clean install', buildInfo: buildInfo
    }

	stage ('Publish build info') {
        server.publishBuildInfo buildInfo
    }
}
    
