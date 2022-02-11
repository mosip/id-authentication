# ID Authentication Configuration Guide

## Overview
The guide here lists down some of the important properties that may be customised for a given installation. Note that the listing here is not exhaustive, but a checklist to review properties that are likely to be different from default.  If you would like to see all the properites, then refer to the files listed below.

## Configuration files
ID Authentication uses the following configuration files:
```
application-default.properties
id-authentication-default.properties
id-authentication-external-default.properties
id-authentication-internal-default.properties
id-authentication-otp-default.properties
identity-mapping.json
```

See [Module Configuration](https://docs.mosip.io/1.2.0/modules/module-configuration) for location of these files.

## Database
```
mosip.ida.database.hostname
mosip.ida.database.port
```

Point the above to your DB and port.  Default is set to point to in-cluster Postgres installed with sandbox.

## Online Verification Partner 
```
ida-auth-partner-id
```

## Keycloak authentication client 
```
mosip.ida.auth.clientId
mosip.ida.auth.secretKey
```

## Keycloak authentication allowed audience 
```
auth.server.admin.allowed.audience
```


## Identity attributes 

```
ida-zero-knowledge-unencrypted-credential-attributes
mosip.preferred.language.attribute.name
mosip.location.profile.attribute.name
ida-default-identity-filter-attributes
ida.id.attribute.separator.fullAddress
```

## Biometric-SDK 
```
mosip.biosdk.default.service.url
mosip.biometric.sdk.providers.finger.mosip-ref-impl-sdk-client.classname
mosip.biometric.sdk.providers.iris.mosip-ref-impl-sdk-client.classname
mosip.biometric.sdk.providers.face.mosip-ref-impl-sdk-client.classname
```

## Demographic SDK 
```
mosip.demographic.sdk.api.classname
mosip.normalizer.sdk.api.classname
```

## Allowed Authentication types 
```
auth.types.allowed
ekyc.auth.types.allowed
internal.auth.types.allowed
```

## Allowed ID types
```
request.idtypes.allowed
request.idtypes.allowed.internalauth
```

## Authentication filter 
```
ida.mosip.external.auth.filter.classes.in.execution.order
ida.mosip.internal.auth.filter.classes.in.execution.order
```

## Child Auth filter
```
mosip.date-of-birth.attribute.name
mosip.date-of-birth.pattern
ida.child-auth-filter.factors.denied
ida.child-auth-filter.child.max.age
```

## Allowed ID types for Hotlist filter
```
mosip.ida.internal.hotlist.idtypes.allowed
```

## Static token enable/disable
```
static.token.enable
```

## Request time validations related 
```
authrequest.received-time-allowed.seconds
authrequest.received-time-adjustment.seconds
authrequest.biometrics.allowed-segment-time-difference-in-seconds
```

## Request Datetime patterns 
```
datetime.pattern
biometrics.datetime.pattern
```

## OTP Flooding 
```
otp.request.flooding.duration
otp.request.flooding.max-count
```

## Allowed enviroments in Authentication request 
```
mosip.ida.allowed.enviromemnts
```

## Allowed domain URIs in Authentication request 
```
mosip.ida.allowed.domain.uris
```

## Notification 
```
notification.uin.masking.charcount
notification.date.format
notification.time.format
```

## Demographic normalisation 
The default Demo-SDK reference implemantation has configurations for normalising name and address for english language, which can be extended for any other languages. Refer to the `id-authentication-default.properties` configuration file for that.




