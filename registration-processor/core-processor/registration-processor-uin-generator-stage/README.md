### registration-processor-uin-generator-stage

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage is to generate uin and mapping the generated uin to the applicant registration Id and store the applicant details in IDA.

##### Default Context Path and Port
```
server.port=8099
eventbus.port=5719
```
##### Configurable Properties from Config Server
```
IDREPOSITORY=${mosip.base.url}/idrepository/v1/identity/
IDREPOGETIDBYUIN=${mosip.base.url}/idrepository/v1/identity/uin
UINGENERATOR=${mosip.base.url}/v1/uingenerator/uin
RETRIEVEIDENTITYFROMRID=${mosip.base.url}/idrepository/v1/identity/rid
RETRIEVEIDENTITY=${mosip.base.url}/idrepository/v1/identity/uin
CREATEVID=${mosip.base.url}/idrepository/v1/vid
registration.processor.id.repo.create=mosip.id.create
registration.processor.id.repo.read=mosip.id.read
registration.processor.id.repo.update=mosip.id.update
registration.processor.id.repo.vidType=Perpetual
registration.processor.id.repo.generate=mosip.vid.create
registration.processor.id.repo.vidVersion=v1
```

