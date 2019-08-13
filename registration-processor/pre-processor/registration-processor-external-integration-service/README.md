### registration-processor-external-integration-service

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

This component validates the Operator, Supervisor, Introducer and User, Machine, Centre details from the Packet

##### Default Context-path and Port
```
server.port=8201
server.servlet.path=/registrationprocessor/v1/eis
```

##### Operations done by the Service
1. It returns boolean value true for every non null requests
