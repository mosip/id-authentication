### registration-processor-bio-dedupe-service


[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This service provides applicantbiometric CBEFF file for a abis reference id.

##### Default Context Path and Port
```
server.port=9097
server.servlet.path=/registrationprocessor/v1/bio-dedupe
```
##### Configurable Properties from Config Server
```
registration.processor.signature.isEnabled=true
```
