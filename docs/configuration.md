# IDA Configuration Guide

## Overview
The guide here lists down some of the important properties that may be customised for a given installation. Note that the listing here is not exhaustive, but a checklist to review properties that are likely to be different from default.  If you would like to see all the properites, then refer to the files listed below.

## Configuration files
IDA uses the following configuration files:
```
application-default.properties
[TODO]
identity-mapping.json
```
The `*-dmz` files are applicable only to [sandbox v2](https://github.com/mosip/mosip-infra/tree/1.2.0-rc2/deployment/sandbox-v2) installation.  [sandbox v3](https://github.com/mosip/mosip-infra/tree/1.2.0-rc2/deployment/v3) does not use them.

The above files are located in [mosip-config](https://github.com/mosip/mosip-config/blob/develop2-v2/) repo

## DB
* `mosip.ida.database.hostname`
* `mosip.ida.database.port`

Point the above to your DB and port.  Default is set to point to in-cluster Postgres installed with sandbox.

