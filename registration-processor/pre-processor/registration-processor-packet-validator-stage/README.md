### registration-processor-packet-validator-stage

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage validates the essentials of a packet before sending the packet for further processing.

##### Default Context Path and Port
```
server.port=8088
eventbus.port=5715
server.servlet.path=/registrationprocessor/v1/packetvalidator
```
##### Configurable Properties from Config Server
```
registration.processor.masterdata.validation.attributes = gender,region,province,city
registration.processor.validateSchema=true
registration.processor.validateFile=true
registration.processor.validateChecksum=true
registration.processor.validateApplicantDocument=false
registration.processor.validateMasterData=false
registration-processor.validatemandotary=true
registration.processor.document.category=IDObject_DocumentCategory_Mapping.json
registration.processor.applicant.type=ApplicantType_Document_Mapping.json
```
##### Validations in Packet Validator
1. Validation of ID Schema : ID Json Validation
2. Validation of Master Data : Based on the key 'registration.processor.validateMasterData' in configuration, the values present in 'registration.processor.masterdata.validation.attributes' are validated against the Master data.
3. Validation of Files : Checking of all files present in hashsequence of packet_meta_info to be actually present inside the packet.
4. Internal Checksum Validation : Matching of checksum received by client with the checksum calculated inside registration-processor.
5. Document Validation : Validation of Documents present in packet_meta_info in correspondance to the value of field 'registration.processor.document.category'.

Note: All these validations can be turned on/off by changing appropriate keys in config server as true/false.
