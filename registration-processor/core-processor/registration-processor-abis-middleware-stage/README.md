### registration-processor-abis-middleware-stage

[Background & Design](https://github.com/mosip/mosip/wiki/Registration-Processor)

[Detailed Process flow](https://github.com/mosip/mosip/blob/master/docs/requirements/FinalProcessFlows/MOSIP_Process%20Flow%201.19%20Reg%20Processor.pdf)

This stage gets insert/identify request created by ABIS Handler and sends them to ABIS Inbound Queues. It also consumes the response from ABIS outbound queues.

##### Defualt context-path and Ports
```
server.port=8091
eventbus.port=5888
```
##### Configurable properties from Configuration-server
```
registration.processor.abis.json=RegistrationProcessorAbis+{<profile>}.json
```
##### Example of RegistrationProcessorAbis_qa.json
```
{
	"abis": [{
			"name": "ABIS1",
			"host": "",
			"port": "",
			"brokerUrl": "tcp://104.211.200.46:61616",
			"inboundQueueName": "abis1-inbound-address_qa",
			"outboundQueueName": "abis1-outbound-address_qa",
			"pingInboundQueueName": "",
			"pingOutboundQueueName": "",
			"userName": "admin",
			"password": "admin",
		        "typeOfQueue": "ACTIVEMQ"
		},
		{
			"name": "ABIS2",
			"host": "",
			"port": "",
			"brokerUrl": "tcp://104.211.200.46:61616",
			"inboundQueueName": "abis2-inbound-address_qa",
			"outboundQueueName": "abis2-outbound-address_qa",
			"pingInboundQueueName": "",
			"pingOutboundQueueName": "",
			"userName": "admin",
			"password": "admin",
			"typeOfQueue": "ACTIVEMQ"
		},
		{
			"name": "ABIS3",
			"host": "",
			"port": "",
			"brokerUrl": "tcp://104.211.200.46:61616",
			"inboundQueueName": "abis3-inbound-address_qa",
			"outboundQueueName": "abis3-outbound-address_qa",
			"pingInboundQueueName": "",
			"pingOutboundQueueName": "",
			"userName": "admin",
			"password": "admin",
			"typeOfQueue": "ACTIVEMQ"
		}
	]

}
```
