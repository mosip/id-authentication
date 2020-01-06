### registration-processor-osi-validator-stage

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

This component validates the Operator, Supervisor, Introducer and User, Machine, Centre details from the Packet

##### Default Context-path and Port
```
server.port=8089
eventbus.port=5716
server.servlet.path=/registrationprocessor/v1/osivalidator
```
##### Configurable properties from Configuration Server
```
mosip.workinghour.validation.required=true
registration.processor.applicant.dob.format=yyyy/MM/dd
mosip.identity.auth.internal.requestid=mosip.identity.auth.internal
```
##### Validations done by the stage
1. User, Centre and Machine Validation :  Validation against the Master-data
2. Centre-Device Validation against the Master-data
3. GPS Validation : Verification whether Latitude and Longitude is present
4. Working Hour Validation : Validating if the packet is created within the working hours of the centre
5. Operator, Supervisor and Introducer Authentication : Either of Operator/Supervisor for adult and introducer for child Packets. Authentication is done by validating the ID, if ID not present then Biometric against ID Authentication.
