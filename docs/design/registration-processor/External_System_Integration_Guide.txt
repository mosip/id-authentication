
# External Stage Integration

**Introudction**

Technical stack used in Registration Processor gives ability to add or change order/sequence of stages/route in the flow. Most of the stages works in isolation, can be deployed independently and does not depend on the previous or next stage in the flow. This design document will helps support team to understand steps to integrate MOSIP with external system using http end point.

This document is intended for advanced option to integrate external system with the MOSIP.

**Prerequisites**
- System integrator need some understanding about vert.x, apache camel and camel bridge
- Also sytem integrator need good understanding about java, spring and JPA
- MOSIP setup and deployment is done successfully

**Set Up Development Environment**
Use below tools and libraries for the development. Versions used for various libraries can be found from [here](https://github.com/mosip/mosip/wiki/Technology-Stack "here")
1. Eclipse or Intellij IDE
2. MOSIP Code
3. Maven installed on machine
4. Java installed on machine

**External System Ingration Options**
Below are the option system integrator team has to integrate external system with MOSIP:

1. *Using Apache Camel HTTP end points*
2. *Using Vert.x Stage*

**1. Using Apache Camel HTTP end points**
1.1	Create REST API service which will be consumed by apache camel
1.2 Download and open MOSIP project sournce code in ecliplse
1.3 Add handler "PacketDetailsRequestHandler.java" in "registration-processor-common-camel-bridge" to feach data and construct request to be send to REST service
1.4 Also add handler "PacketDetailResponseHandler.java" in "registration-processor-common-camel-bridge" to read REST API response and construct payload which will be passed to stage
1.5 Open "registration-processor-camel-routes.xml" from "config" project from MOSIP source code and update it by adding handlers and REST end point as shown below:

```xml
<route id="packet-validator-->osi-validator route">
		<from uri="vertx:packet-validator-bus-out" />
		<log
			message="packet-validator-->osi-validator route ${bodyAs(String)}" />
		<choice>
			<when>
				<simple>${bodyAs(String)} contains '"isValid":true'</simple>
				<to uri="bean:packetDetailsRequestHandler"/>
				<setHeader headerName="CamelHttpMethod">
			      <constant>GET</constant>
			    </setHeader>
				<to uri="http://domain.name/registration/packetdetails" />
				<to uri="bean:packetDetailResponseHandler"/>
				<to uri="vertx:osi-validator-bus-in" />
			</when>
			<when>
				<simple>${bodyAs(String)} contains '"isValid":false'</simple>
				<to uri="vertx:message-sender-bus" />
			</when>
			<when>
				<simple>${bodyAs(String)} contains '"internalError":true'</simple>
				<to uri="vertx:retry" />
			</when>
			<otherwise>
				<to uri="vertx:error" />
			</otherwise>
		</choice>
	</route>
```
1.6 Commit changes and deploy project
1.7 Logical view after adding changes:

![abis-http-external-integration-logical-view](_images/abis-http-external-integration-logical-view.png)