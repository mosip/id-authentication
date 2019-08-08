### registration-processor-retry-stage

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

This component processes the packet in a particular stage again when it fails due to external resource exceptions.

##### Default Context-path and Port
```
server.port=8090
health.config.enabled=false
eventbus.port=5723
```
##### Configurable properties from Configuration Server
```
registration.processor.wait.period=1
```
