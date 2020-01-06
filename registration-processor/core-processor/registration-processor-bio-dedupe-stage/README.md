### registration-processor-bio-dedupe-stage

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage processes any request that comes to it based on Registration Type and the biometric uniqueness will be verified through ABIS and appropriate DB statuses will be updated
##### Default Context Path and Port
```
server.port=9096
eventbus.port=5718
server.servlet.path=/registrationprocessor/v1/biodedupe
```

