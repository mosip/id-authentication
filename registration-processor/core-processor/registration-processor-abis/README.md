### registration-processor-abis

[Background & design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Process Flow for Registration-Processor](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage acts as dummy Abis system. It has 2 functionalities.

i) Insert

ii) Identify

The dummy Abis fetches the request from the Inbound Queue, processes it as per either Insert or Identify request, and puts the response back to the Outbound Queue.

##### Default Context Path and Port
```
server.port=9098
server.servlet.path=/abis
```
##### Configurable Properties from Config Server
```
TESTFINGERPRINT=ns2:TestFinger
#Dummy Tag for iris in cbeff file
TESTIRIS=ns2:TestIris
#Dummy Tag for face in cbeff file
TESTFACE=ns2:TestFace
```
