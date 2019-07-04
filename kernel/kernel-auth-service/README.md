## kernel-auth-service


[Background & Design](../../docs/design/kernel/kernel-authn.md)

[Setup Guide](https://github.com/mosip/mosip/wiki/Auth-Implementation)

[Api Documentation](https://github.com/mosip/mosip/wiki/AuthN-&-AuthZ-APIs)


Default Port and Context Path

```
server.port=8091
server.servlet.path=/authmanager

```

localhost:8091/authmanager/swagger-ui.html


**Application Properties**

```
auth.jwt.secret=authjwtsecret
auth.jwt.base=Mosip-Token
auth.jwt.expiry=1800000
auth.token.header=Authorization
auth.token.sliding.window.exp=-10
auth.refreshtoken.header=RefreshToken
auth.jwt.refresh.expiry=86400000

#spring.datasource.url=jdbc:postgresql://dbhost:port/mosip_iam
#spring.datasource.username=iamuser
#spring.datasource.password=pwd
#spring.datasource.driverClassName=org.postgresql.Driver


otp.manager.api.generate=https://localhost:port/v1/otpmanager/otp/generate
otp.manager.api.verify=https://localhost:port/v1/otpmanager/otp/validate
otp.sender.api.email.send=https://localhost:port/v1/emailnotifier/email/send
otp.sender.api.sms.send=https://localhost:port/v1/smsnotifier/sms/send
masterdata.api.template=https://localhost:port/v1/masterdata/templates
masterdata.api.template.otp=/otp-sms-template
idrepo.api.getuindetails=https://qa.mosip.io/v1/idrepo/identity/{uin}

auth.server.validate.url=https://localhost:port/v1/authmanager/authorize/validateToken

auth.primary.language=eng

mosip.notification.language-type=BOTH

mosip.primary-language=eng
mosip.secondary-language=fra

mosip.kernel.auth.app.id=authserver
mosip.kernel.auth.client.id=auth_server_id
mosip.kernel.auth.secret.key=auth_secret_key

datastores=ldap_1_DS,db_1_DS,db_2_DS

preregistration_datasource=db_1_DS
registrationclient_datasource=ldap_1_DS
registrationprocessor_datasource=ldap_1_DS
ida_datasource=ldap_1_DS
authserver_datasource=ldap_1_DS


db_1_DS.datastore.ipaddress=jdbc:postgresql://dbhost:port/mosip_iam
db_1_DS.datastore.port=9001
db_1_DS.datastore.username=iamuser
db_1_DS.datastore.password=pwd
db_1_DS.datastore.driverClassName=org.postgresql.Driver
db_1_DS.datastore.schema=GOVT_OFFICERS

db_2_DS.datastore.ipaddress=jdbc:postgresql://dbhost:port/mosip_iam
db_2_DS.datastore.port=9001
db_2_DS.datastore.username=iamuser
db_2_DS.datastore.password=pwd
db_2_DS.datastore.driverClassName=org.postgresql.Driver
db_2_DS.datastore.schema=GOVT_OFFICERS

ldap_1_DS.datastore.ipaddress=ldapIP
ldap_1_DS.datastore.port=10389

ldap.admin.dn=uid=admin,ou=system
ldap.admin.password=secret
ldap.userdn.prefix=uid=
ldap.userdn.suffix=,ou=people,c=mindtree
ldap.roles.base=ou=roles,c=morocco
ldap.roles.search.prefix=(&(objectClass=organizationalRole)(roleOccupant=
ldap.roles.search.suffix=))
ldap.roles.class=(objectClass=organizationalRole)

```
