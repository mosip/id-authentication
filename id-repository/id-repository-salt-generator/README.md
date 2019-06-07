## id-repository-salt-generator


[Background & Design](../../docs/design/idrepository/salt-generator.md)

[Api Documentation]( https://github.com/mosip/mosip/wiki/ID-Repository-API)

Default Port and Context Path

```
server.port=8092
```


**Application Properties**

[id-repository-dev.properties](https://github.com/mosip/mosip-configuration/blob/0.12.0/config/id-repository-dev.properties)


```
application.id=id-repository
mosip.idrepo.application.id=ID_REPO
mosip.idrepo.application.name=ID-Repository
mosip.idrepo.application.version=v1
mosip.idrepo.application.version.pattern=^v\\d+(\\.\\d+)?$
mosip.idrepo.modulo-value=1000
mosip.idrepo.datetime.timezone=GMT
mosip.idrepo.identity.json.path=identity.UIN

mosip.idrepo.identity.uin-status.registered=ACTIVATED
mosip.idrepo.identity.uin-status=ACTIVATED,BLOCKED,DEACTIVATED 

#----------------------------------FSAdapter HDFS------------------------------------------
mosip.kernel.fsadapter.hdfs.user-name=idrepo-dev

management.endpoint.restart.enabled=true

mosip.idrepo.identity.allowedTypes=bio,demo,all
mosip.idrepo.identity.allowedBioAttributes=individualBiometrics
mosip.idrepo.identity.bioAttributes=individualBiometrics,parentOrGuardianBiometrics

mosip.idrepo.identity.id.create=mosip.id.create
mosip.idrepo.identity.id.read=mosip.id.read
mosip.idrepo.identity.id.update=mosip.id.update

# *********** REST-services *****************
# Kernel-Audit
mosip.idrepo.audit.rest.uri=https://dev.mosip.io/v1/auditmanager/audits
mosip.idrepo.audit.rest.httpMethod=POST
mosip.idrepo.audit.rest.headers.mediaType=application/json

mosip.idrepo.encryptor.rest.uri=https://dev.mosip.io/v1/cryptomanager/encrypt
mosip.idrepo.decryptor.rest.uri=https://dev.mosip.io/v1/cryptomanager/decrypt
mosip.idrepo.encryptor.rest.httpMethod=POST
mosip.idrepo.decryptor.rest.httpMethod=POST
mosip.idrepo.encryptor.rest.headers.mediaType=application/json
mosip.idrepo.decryptor.rest.headers.mediaType=application/json
#timeout In seconds
mosip.idrepo.encryptor.rest.timeout=1
mosip.idrepo.decryptor.rest.timeout=1

#database mappings idrepo
mosip.idrepo.identity.db.shard.url=jdbc:postgresql://104.211.208.136:9001/mosip_idrepo
mosip.idrepo.identity.db.shard.username=idrepouser
mosip.idrepo.identity.db.shard.password={cipher}AQBjo/GFdsDQAzHOBQ0vpF6DXCz/9U7PHbfXOL2kR9eliwsgXW+Zsek0ttKfQU/saf+2DqjPIKsXFJ9f0nbybA5nOnZtPIZDceiReLWi3CY4nk+y7SCaRwh5eZI4Hd/UKfrESE1jqtckMO+YoYWc81RMIlLkS/lrf1QgmsDUPCnZFcDT3eopaP2+zF9y2LZSL6Rzy2GaLosOKbUJMiHsQU5j6AfPoG9LKKM3G6kX2UHTzJ4a5nMYE1lORqL+t0E8gQ00E7l2q2fXANcEIBgUZ5esSUjeXF5nLbbHqg9OASMDYokeJ8X+Wc2bm3J9ARquRieTRwE9mm4Np3mhXiqNjQUYrCUwz+ZcoiA0+vDlH9E0SmZhVZZwcmeZqiRl8LDbm6I=
mosip.idrepo.identity.db.shard.driverClassName=org.postgresql.Driver

#---------------------------------VID Service--------------------------------------------------#
mosip.idrepo.vid.id.create=mosip.vid.create
mosip.idrepo.vid.id.read=mosip.vid.read
mosip.idrepo.vid.id.update=mosip.vid.update
mosip.idrepo.vid.id.regenerate=mosip.vid.regenerate 

mosip.idrepo.vid.policy-schema-url=${mosip.kernel.idobjectvalidator.file-storage-uri}mosip-vid-policy-schema.json
mosip.idrepo.vid.policy-file-url=${mosip.kernel.idobjectvalidator.file-storage-uri}mosip-vid-policy.json

mosip.idrepo.vid.db.url=jdbc:postgresql://104.211.208.136:9001/mosip_idmap
mosip.idrepo.vid.db.username=idmapuser
mosip.idrepo.vid.db.password={cipher}AQBjo/GFdsDQAzHOBQ0vpF6DXCz/9U7PHbfXOL2kR9eliwsgXW+Zsek0ttKfQU/saf+2DqjPIKsXFJ9f0nbybA5nOnZtPIZDceiReLWi3CY4nk+y7SCaRwh5eZI4Hd/UKfrESE1jqtckMO+YoYWc81RMIlLkS/lrf1QgmsDUPCnZFcDT3eopaP2+zF9y2LZSL6Rzy2GaLosOKbUJMiHsQU5j6AfPoG9LKKM3G6kX2UHTzJ4a5nMYE1lORqL+t0E8gQ00E7l2q2fXANcEIBgUZ5esSUjeXF5nLbbHqg9OASMDYokeJ8X+Wc2bm3J9ARquRieTRwE9mm4Np3mhXiqNjQUYrCUwz+ZcoiA0+vDlH9E0SmZhVZZwcmeZqiRl8LDbm6I=
mosip.idrepo.vid.db.driverClassName=org.postgresql.Driver

mosip.idrepo.vid.active-status=ACTIVE
mosip.idrepo.vid.unlimited-txn-status=USED
mosip.idrepo.vid.regenerate.allowed-status=ACTIVE,REVOKED,EXPIRED,USED
mosip.idrepo.vid.allowedstatus=ACTIVE,REVOKED,EXPIRED,USED,INVALIDATED,DEACTIVATED

mosip.idrepo.retrieve-by-uin.rest.uri=https://dev.mosip.io/idrepository/v1/identity/uin/{uin}
mosip.idrepo.retrieve-by-uin.rest.httpMethod=GET
mosip.idrepo.retrieve-by-uin.rest.headers.mediaType=application/json

#---------------------------------Salt Generator--------------------------------------------------#
mosip.idrepo.salt-generator.start-sequence=0
mosip.idrepo.salt-generator.end-sequence=999
mosip.idrepo.salt-generator.chunk-size=10

```
