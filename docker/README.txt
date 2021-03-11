Place the two scripts (configure_biosdk.sh, configure_softhsm.sh) at the artifactory at below location:
$artifactory_url_env/artifactory/libs-release-local/deployment/docker/id-authentication/

These scripts will be sourced by the configure_start.sh scripts  for the required IDA docker services that is ran during docker run/pod start.
