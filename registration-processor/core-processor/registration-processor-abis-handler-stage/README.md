### registration-processor-abis-handler-stage

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage takes the count of abis devices and creates that many insert and identify request, saving them in the AbisRequest table for abis middleware to use.

##### Default Context Path and Port
```
server.port=9071
eventbus.port=5726
```
##### Configurable Properties from Config Server
```
registration.processor.biometric.reference.url=${mosip.base.url}/registrationprocessor/v1/bio-dedupe/biometricfile
registration.processor.abis.maxResults=30
registration.processor.abis.targetFPIR=30
```

