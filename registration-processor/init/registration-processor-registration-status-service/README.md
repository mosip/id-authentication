### registration-processor-registration-status-service

[Background & Design](https://github.com/mosip/mosip/wiki/Registration-Processor)

This component enables syncing of packet(s) and getting status of packet(s) via REST Api.

[API Specification](https://github.com/mosip/mosip/wiki/Registration-Processor-APIs#2-registration-status-service)

##### Default Context-path and Port

```
server.port=8083
server.servlet.path=/registrationprocessor/v1/registrationstatus

```

##### Configurable Properties from Config Server

```
registration.processor.max.retry=3
mosip.registration.processor.registration.status.id=mosip.registration.status
mosip.registration.processor.registration.sync.id=mosip.registration.sync

```
