### registration-processor-quality-checker-stage

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage validates the quality scores of the applicant biometric types.

##### Default Context Path and Port
```
server.port=9072
eventbus.port=5727
```
##### Configurable Properties from Config Server
```
mosip.registration.iris_threshold=70
mosip.registration.leftslap_fingerprint_threshold=80
mosip.registration.rightslap_fingerprint_threshold=80
mosip.registration.thumbs_fingerprint_threshold=80
mosip.registration.facequalitythreshold=25
```
##### Validations in Quality Checker Stage
1. Validation of all the quality values of biometric types of applicant cbeff with the values from config server. Passing the stage if 
all qualities are greater than or equal to threshold quality values mentioned in config server for each biometric types.

