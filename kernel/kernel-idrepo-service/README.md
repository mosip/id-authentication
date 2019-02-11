## kernel-idrepo-service


[Background & Design](../../docs/design/kernel/kernel-idrepo.md)

[Api Documentation]( https://github.com/mosip/mosip/wiki/ID-Repository-API)

Default Port and Context Path

```
server.port=8090
server.servlet.path=/idrepo
```
localhost:8090/idrepo/swagger-ui.html


**Application Properties**

[kernel-idrepo-service-dev.properties](../../config/kernel-idrepo-service-dev.properties)


```
application.version=1.0
mosip.kernel.idrepo.dfs.access-key=345234534535asdf435
mosip.kernel.idrepo.dfs.secret-key=sdgdfg534gdfgfgdfg
mosip.kernel.idrepo.dfs.endpoint=http://dfshost:9999
management.endpoint.restart.enabled=true

mosip.kernel.idrepo.primary-lang=ARA
mosip.kernel.idrepo.secondary-lang=FRE

#Kernel-JsonValidator
mosip.kernel.jsonvalidator.property-source=CONFIG_SERVER
mosip.kernel.idrepo.json-schema-fileName=mosip-identity-json-schema.json

#changed datasources
mosip.kernel.idrepo.db.shard1.url=jdbc:postgresql://dbhost1:8888/mosip_idrepo
mosip.kernel.idrepo.db.shard1.username=dbuser1
mosip.kernel.idrepo.db.shard1.password=dbpwd1
mosip.kernel.idrepo.db.shard1.driverClassName=org.postgresql.Driver
mosip.kernel.idrepo.db.shard2.url=jdbc:postgresql://dbhost2:8888/mosip_idrepo
mosip.kernel.idrepo.db.shard2.username=dbuser2
mosip.kernel.idrepo.db.shard2.password=dbpwd2
mosip.kernel.idrepo.db.shard2.driverClassName=org.postgresql.Driver

spring.jpa.database=default
#spring.jpa.generate-ddl=true
#spring.jpa.show-sql=true
#spring.jpa.properties.hibernate.format_sql=true
#logging.level.org.hibernate.SQL=DEBUG
#logging.level.org.hibernate.type=TRACE

mosip.kernel.idrepo.status.registered=ACTIVATED
mosip.kernel.idrepo.status=BLOCKED, DEACTIVATED

mosip.kernel.idrepo.allowedTypes=bio,demo,all
mosip.kernel.idrepo.allowedBioTypes=individualBiometrics

mosip.kernel.idrepo.id.create=mosip.id.create
mosip.kernel.idrepo.id.read=mosip.id.read
mosip.kernel.idrepo.id.update=mosip.id.update

datetime.pattern=yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
datetime.timezone=GMT

#Added keymanager url
mosip.kernel.cryptomanager.encrypt.url=https://host/cryptomanager/v1.0/encrypt
mosip.kernel.cryptomanager.decrypt.url=https://host/cryptomanager/v1.0/decrypt
application.id=ID_REPO
application.version=1.0
mosip.kernel.jsonvalidator.file-storage-uri=${spring.cloud.config.uri}/${spring.application.name}/${spring.profiles.active}/${spring.cloud.config.label}/

```
