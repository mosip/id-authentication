## id-repository

[Background & Design](../../docs/design/idrepository/identity-service.md)

[Background & Design](../../docs/design/idrepository/vid-service.md)

[Background & Design](../../docs/design/idrepository/salt-generator.md)

[Api Documentation]( https://github.com/mosip/mosip/wiki/ID-Repository-API)

**Application Properties**

[id-repository-dev.properties](https://github.com/mosip/mosip-configuration/blob/0.12.0/config/id-repository-dev.properties)


```
Build and Deployment commands:
docker run -it -d -p 8092:8092 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} -e schema_name={schema} -e table_name={table} docker-registry.mosip.io:5000/id-repository-salt-generator

docker run -it -d -p 8090:8090 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} docker-registry.mosip.io:5000/id-repository-identity-service

docker run -it -d -p 8091:8091 -e active_profile_env={profile}  -e spring_config_label_env= {branch} -e spring_config_url_env={config_server_url} docker-registry.mosip.io:5000/id-repository-vid-service

```
