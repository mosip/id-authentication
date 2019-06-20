
registration-processor-external-stage

Background & design

This stage integrates with external system for required external operations

Default Context Path and Port

eventbus.port=5736
server.port=8095
server.servlet.path=/registrationprocessor/v1/externaleventbus.port=5736

Configurable Properties from Config Server
EISERVICE=${mosip.base.url}/registrationprocessor/v1/eis/registration-processor/external-integration-service/v1.0

Operations in External stage
External validation by sending requests to external integration system
