## id-repository-service


[Background & Design](../../docs/design/kernel/kernel-idrepo.md)

[Api Documentation]( https://github.com/mosip/mosip/wiki/ID-Repository-API)

Default Port and Context Path

```
server.port=8090
server.servlet.path=/idrepository/v1
```
localhost:8090/idrepository/v1/swagger-ui.html


**Application Properties**

[id-repository-dev.properties](../../config/id-repository-dev.properties)


```
application.version=1.0
mosip.idrepo.dfs.access-key=345234534535asdf435
mosip.idrepo.dfs.secret-key=sdgdfg534gdfgfgdfg
mosip.idrepo.dfs.endpoint=http://dfshost:9999
management.endpoint.restart.enabled=true

mosip.idrepo.primary-lang=ARA
mosip.idrepo.secondary-lang=FRE

#Kernel-IdObjectValidator
mosip.IdObjectValidator.property-source=CONFIG_SERVER
mosip.idrepo.json-schema-fileName=mosip-identity-json-schema.json

#changed datasources
mosip.idrepo.db.shard1.url=jdbc:postgresql://dbhost1:8888/mosip_idrepo
mosip.idrepo.db.shard1.username=dbuser1
mosip.idrepo.db.shard1.password=dbpwd1
mosip.idrepo.db.shard1.driverClassName=org.postgresql.Driver
mosip.idrepo.db.shard2.url=jdbc:postgresql://dbhost2:8888/mosip_idrepo
mosip.idrepo.db.shard2.username=dbuser2
mosip.idrepo.db.shard2.password=dbpwd2
mosip.idrepo.db.shard2.driverClassName=org.postgresql.Driver

spring.jpa.database=default
#spring.jpa.generate-ddl=true
#spring.jpa.show-sql=true
#spring.jpa.properties.hibernate.format_sql=true
#logging.level.org.hibernate.SQL=DEBUG
#logging.level.org.hibernate.type=TRACE

mosip.idrepo.status.registered=ACTIVATED
mosip.idrepo.status=BLOCKED, DEACTIVATED

mosip.idrepo.allowedTypes=bio,demo,all
mosip.idrepo.allowedBioTypes=individualBiometrics

mosip.idrepo.id.create=mosip.id.create
mosip.idrepo.id.read=mosip.id.read
mosip.idrepo.id.update=mosip.id.update

datetime.pattern=yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
datetime.timezone=GMT

#Added keymanager url
mosip.cryptomanager.encrypt.url=https://host/cryptomanager/v1.0/encrypt
mosip.cryptomanager.decrypt.url=https://host/cryptomanager/v1.0/decrypt
application.id=ID_REPO
application.version=1.0
mosip.kernel.IdObjectValidator.file-storage-uri=${spring.cloud.config.uri}/${spring.application.name}/${spring.profiles.active}/${spring.cloud.config.label}/

```
