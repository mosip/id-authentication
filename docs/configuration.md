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

The above files are located in [mosip-config](https://github.com/mosip/mosip-config) repo

## DB
* `mosip.ida.database.hostname`
* `mosip.ida.database.port`

Point the above to your DB and port.  Default is set to point to in-cluster Postgres installed with sandbox.

