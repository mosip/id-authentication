### registration-processor-print-service

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage provides downloadable pdf for a uin or rid.

##### Default Context Path and Port
```
server.port=9099
server.servlet.path=/registrationprocessor/v1/print
```
##### Configurable Properties from Config Server
```
mosip.registration.processor.print.service.id=mosip.registration.print
```
