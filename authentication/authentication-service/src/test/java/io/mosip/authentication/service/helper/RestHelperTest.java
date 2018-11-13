node {
  def branch = 'DEV'
  def projectToBuild = 'child1/'
  stage('Checkout') {
    dir(branch) {
      checkout([$class: 'GitSCM',
                branches: [[name: branch]],
                userRemoteConfigs: [[url:'https://github.com/Swatikp/Maven-parent-child-sample', credentialsId:'4c9741a2-4c15-4aad-a13d-3a1206u05321']],
                extensions: [
                  
                  [$class: 'PathRestriction',excludedRegions: '', includedRegions: 'child1/.*'],
                  [$class: 'SparseCheckoutPaths',  sparseCheckoutPaths:[[$class:'SparseCheckoutPath', path:projectToBuild]]]
                   
                ],
               ])
    }
  }
  	stage('Build') {
  	            sh "ls"
  	            sh "ls DEV"
				List<String> changedModules = ArrayList<String>;
				List<String> modulesContainingDockerFileArray = ArrayList<String>;
                sh "/opt/apache-maven-3.3.3/bin/mvn -f '$branch/$projectToBuild' package"
                sh "echo child 01"
                def changeLogSets = currentBuild.changeSets
                for (int i = 0; i < changeLogSets.size(); i++) {
                    def entries = changeLogSets[i].items
                    for (int j = 0; j < entries.length; j++) {
                        def entry = entries[j]
                        echo "${entry.commitId} by ${entry.author} on ${new Date(entry.timestamp)}: ${entry.msg}"
                        def paths = new ArrayList(entry.affectedPaths)
                        for (int k = 0; k < paths.size(); k++) {
                            def path = paths[k]
                            echo "${path}"
							changedModules.add(path);
							echo changedModules.get( 0 ).toString();
                        }
                    }
                }
				modulesContainingDockerFileArray= sh"(`find . -name 'Dockerfile'`)"
				echo modulesContainingDockerFileArray.get( 0 ).toString();
                
	}
  }
