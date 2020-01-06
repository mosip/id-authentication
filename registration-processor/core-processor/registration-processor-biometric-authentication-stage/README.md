##### registration-processor-biometric-authentication stage

[Background & Desgin](https://github.com/mosip/mosip/wiki/Registration-Processor)
[Detailed Process flow](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This component validates update packets in case of adult registration.

##### Default Port
```
server.port=8020
eventbus.port=5777
```
##### Description of Validation

Checking whether the 'individualBiometrics' file is present, if not present, implies the packet to be a demographic update packet. We check 'authenticationBiometricFileName' and validate it against IDA.
