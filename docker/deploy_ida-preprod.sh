#!/bin/bash
version=1.0.9
label=1.0.9
repo=docker-registry.mosip.io:5000
#artifactory_url=http://13.71.87.138:8040/artifactory/list/libs-snapshot-local
artifactory_url=https://oss.sonatype.org/service/local/repositories/releases/content
conf_file_name=softhsm-application.conf
release_zip_file=release.zip
#access_token=#<access token>
#release_artifact_id=#<release-artifact-id-in-github-action>
git_repo_name=id-authentication
git_repo_user=mosip
git_branch=1.0.10

image_suffix=( -softhsm -softhsm -softhsm -softhsm )
dockerfile_suffix=( -softhsm_based -softhsm_based -softhsm_based -softhsm_based )

modules=('authentication-service' 'authentication-internal-service' 'authentication-kyc-service' 'authentication-otp-service')

modules_to_build=($1 $2 $3 $4)

if [ "${#modules_to_build[@]}" == 0 ]; then
  echo "No modules specified.. building all"
  modules_to_build=${modules[@]}
fi

ports=( 8090 8093 8091 8092 )

current_module=
current_port=

runall () {
	echo "Started..."
	echo_configs

	clean_target
	#download_release_jar
	
	for i in "${!modules[@]}"; do 
		# To print index, ith 
		# element 
		current_module=${modules[$i]}
		
		if `contains "$modules_to_build" "$current_module"` ; then
		
			echo "Current Module - $current_module "
		
			current_port=${ports[$i]}
			current_image_suffix=${image_suffix[$i]}
			current_dockerfile_suffix=${dockerfile_suffix[$i]}
			
			current_docker_file=Dockerfile${current_dockerfile_suffix}
			current_docker_name=${current_module}${current_image_suffix}
			current_docker_tag=${version}
			current_docker_image=${current_docker_name}:${current_docker_tag}
			
			run_for_current_module
		fi
		
	done
	
	echo "Finished."
}

contains() {
  local list="$1"
  local item="$2"
  if [[ $list =~ (^|[[:space:]])"$item"($|[[:space:]]) ]] ; then
    # yes, list include item
    result=0
  else
    result=1
  fi
  return $result
}

run_for_current_module() {
	echo Buidling $current_module
	cleanup
	build
	tag_image
	push_image
	#run_image_from_local
	#run_image_from_repo
	echo $current_module built

}

echo_configs() {
	echo "Configs::"
	echo "Version: $version"
	echo "Repository: $repo"
	echo "Artifactory: $artifactory_url"
	echo "Config File Name: $conf_file_name"
	echo "Modules: "
	for i in "${!modules_to_build[@]}"; do 
		echo ${modules_to_build[$i]} 
	done

}

funcs() {
	echo "IDA Deployment Functions:"
	echo "cleanup"
	echo "stop_and_remove_existing_containers"
	echo "remove_existing_images"
	echo "build"
	echo "setup_build"
	echo "download_jars"
	echo "build_images"
	echo "tag_images"
	echo "download_jars"
	echo "copy_release_jars"
	echo "push_images"
	echo "run_images_from_local"
	echo "run_images_from_repo"
	echo "runall"
	echo "funcs"

}

cleanup() {
	echo "Cleanup:"
	stop_and_remove_existing_container
	remove_existing_image
}

stop_and_remove_existing_container() {
	echo "Stop and Remove Existing containers:"
	existing_container_ids=$(docker ps | grep ${current_docker_name} | grep ${current_docker_tag} | awk '{ print $1 }')
	for i in $existing_container_ids
	do
	   echo "stopping container $i"
	   docker stop $i
	done
	
	existing_container_ids=$(docker ps | grep ${current_docker_name} | grep ${current_docker_tag} | awk '{ print $1 }')
	for i in $existing_container_ids
	do
	   echo "removing container $i"
	   docker rm $i
	done
}

remove_existing_image() {
	echo "Remove Existing Images:"
	existing_image_ids=$(docker images | grep ${current_docker_name} | grep ${current_docker_tag} | awk '{ print $3 }')
	for i in $existing_image_ids
	do
	   echo "removing image $i"
	   docker rmi $i
	done
}

build() {
	#clean_jar
	setup_build
	build_image
	#clean_docker_file
	#clean_jar
	#clean_target
}

setup_build() {
	echo "Set up..."
	mkdir -p target
	download_jar
	#download_release_jar
	#copy_release_jar
	create_conf_file
	download_dockerfile
}

clean_docker_file() {
	rm -rf $current_docker_file
}

clean_jar() {
	echo Cleaning jar for $current_module
	rm -rf target/$current_module-*
}

clean_target() {
	rm -rf target/
}

download_release_jar() {
	curl -v -L -u octocat:$access_token -o release.zip https://api.github.com/repos/mosip/id-authentication/actions/artifacts/$release_artifact_id/zip
}

copy_release_jar() {
	if [ -f $release_zip_file ]
	then
		echo "Copying Release Jar - $current_module"
		working_dir=`pwd`
		cd target/
    pwd
		unzip -o $working_dir/$release_zip_file -d ./
    unzip -o release.zip
		mv authentication/$current_module/target/$current_module-*.jar ./
		rm authentication/* -rf
		rmdir authentication/
		#rm -f release.zip
		cd $working_dir
	fi
}

download_jar() {
	echo "Downloading Jar... $current_module"
	wget -q $artifactory_url/io/mosip/authentication/$current_module/$version/$current_module-$version.jar -O target/$current_module-$version.jar
}

create_conf_file() {
	if [ ! -f $conf_file_name ] 
	then
		echo "Creating conf file..."
		cat <<EOF >$conf_file_name
name=SoftHSM
library=/usr/local/lib/softhsm/libsofthsm2.so
slotListIndex=0
EOF
	fi

}

download_dockerfile() {
	echo Downloading Dockerfile for $current_module
	wget https://raw.githubusercontent.com/$git_repo_user/$git_repo_name/$git_branch/authentication/$current_module/$current_docker_file -O $current_docker_file

}


build_image() {
	echo "Building image ${current_docker_image}"
	docker build -t ${current_docker_image} -f $current_docker_file .

}


tag_image() {
	echo "Tagging image $repo/${current_docker_image}"

	docker tag ${current_docker_image} $repo/${current_docker_image}
}

push_image() {
	echo "Pushing image $repo/${current_docker_image}"

	docker push $repo/${current_docker_image}

}

run_image_from_local() {
	echo "Running local image for $current_module"

	docker run --rm -d -p $current_port:$current_port -v /softhsm:/softhsm/var/lib/softhsm/ -e spring_config_label_env=$label -e active_profile_env=dev -e spring_config_url_env=http://104.211.212.28:51000 ${current_docker_image}
}

run_image_from_repo() {
	echo "Running repo image for $current_module"

	docker run --rm -d -p $current_port:$current_port -v /softhsm:/softhsm/var/lib/softhsm/ -e spring_config_label_env=$label -e active_profile_env=dev -e spring_config_url_env=http://104.211.212.28:51000 $repo/${current_docker_image}

}


runall

