### registration-processor-demo-dedupe-stage
[Background & Design](https://github.com/mosip/mosip/wiki/Registration-Processor)
[Detailed Process Flow](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stages saves the Demographic data, i.e, Name, DOB and Gender. Post saving it performs Deduplication using exact one to one match of these parameters.


**** Important NOTE ****
The demo dedupe will be performed on exact match of name, date of birth and gender. If additional fields need to be included for demo dedupe match then code need to be modified. 

##### Default context-path and Ports
```
server.port=8091
eventbus.port=5717
server.servlet.path=/registrationprocessor/v1/demodedupe
```
##### Configurable Properties from Configuration Server
```
application.id=REGISTRATION
registration.processor.demodedupe.manualverification.status=REJECTED
```
