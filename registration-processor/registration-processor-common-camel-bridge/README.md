## registration-processor-common-camel-bridge

[Design & Background for Registration-Processor](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Orchestration & Workflow in MOSIP](https://github.com/mosip/mosip/blob/0.12.0/docs/design/registration-processor/orchestration_workflow.md)

registration-processor-common-camel-bridge is the orchestration module for MOSIP Registration Processor. 
It is responsible for routing messages between stages as per the configured flow.
camel-bridge works as per the zones in MOSIP. The 2 zones defined are

1. dmz - This is the demilitarized zone which has stages that interacts with outside the server systems.
2. secure - These are the stages of registration-processor which are isolated from outside systems considering security of the system.

Default Configuration
```
registration.processor.zone=dmz
eventbus.port=5723
```
Config Server Properties
```
cluster.manager.file.name=hazelcast_secure.xml
dmz.cluster.manager.file.name=hazelcast_dmz.xml
camel.dmz.active.flows.file.names=registration-processor-camel-routes-new-dmz.xml,registration-processor-camel-routes-update-dmz.xml,registration-processor-camel-routes-activate-dmz.xml,registration-processor-camel-routes-res_update-dmz.xml,registration-processor-camel-routes-deactivate-dmz.xml,registration-processor-camel-route-lost-dmz.xml
camel.secure.active.flows.file.names=registration-processor-camel-routes-new-secure.xml,registration-processor-camel-routes-update-secure.xml,registration-processor-camel-routes-activate-secure.xml,registration-processor-camel-routes-res_update-secure.xml,registration-processor-camel-routes-deactivate-secure.xml,registration-processor-camel-route-lost-secure.xml

```
#### Different Process flows for Registration-Processor

Routing of events are based on defined flows, which as of now are new, update, res_update, activate, deactivate and lost.
Each of the flow needs a corresponding routing xml, for e.g [registration-processor-camel-routes-new-secure.xml](https://github.com/mosip/mosip-configuration/blob/0.12.0/config/registration-processor-camel-routes-new-secure-qa.xml)
The active flow file names should individually be present in config server along with active profile, for e.g, for new flow it will be registration-processor-camel-routes-new-secure-dev.xml  when the profile is dev, and changes accordingly.
