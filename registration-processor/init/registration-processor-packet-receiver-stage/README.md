### registration-processor-packet-receiver-stage
[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

This component supports upload of packet(s) through rest api.

[API Specification](https://github.com/mosip/mosip/wiki/Registration-Processor-APIs#1-packet-receiver-service)

##### Default Context-path and Port

```
server.port=8081
server.servlet.path=/registrationprocessor/v1/packetreceiver

```

##### Configurable Properties from Config Server

```
registration.processor.max.file.size=5
mosip.registration.processor.application.version=1.0
mosip.registration.processor.datetime.pattern=yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
mosip.registration.processor.timezone=GMT
mosip.registration.processor.packet.id=mosip.registration.packet

```
