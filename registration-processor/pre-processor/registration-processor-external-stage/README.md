
### registration-processor-external-stage

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage integrates with external system for required external operations

##### Default Context Path and Port
```
eventbus.port=5736
server.port=8095
server.servlet.path=/registrationprocessor/v1/externaleventbus.port=5736
```
##### Configurable Properties from Config Server
```
EISERVICE=${mosip.base.url}/registrationprocessor/v1/eis/registration-processor/external-integration-service/v1.0
```
##### Operations in External stage
External validation by sending requests to external integration system
