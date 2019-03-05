
# Add HTTP stage 

**Background**
Technical stack used in Registration Processor gives ability to add or change order/sequence of stages/route in the flow. Most of the stages works in isolation, can be deployed independently and does not depend on the previous or next stage in the flow. This design helps support team with the steps to create and add or remove HTTP apache camel route/stage.

**The target users are -**
Product technical support team.

**The key requirements are -**
-	Once MOSIP as a product is released as an open source for anyone to use it, product should have sample code for stages and apache camel DSL xml along with document with the steps to add or remove them.

**Apache Camel Bridge -**
All the vertx stages in registration process are arranged in a particular sequence using apache camel bridge. Though technically it is possible to add or remove stages in the camel bridge but in some cases this sequece or order can not be altered due to highly dependent activity. For example Packet Receiver, Virous Scanner and Packet Uploader need to work in sequence. 

**The key non-functional requirements are**
1.	Addition of a additional HTTP route should be done without updating any existing stages.
2.  Auditing of the all the transactions including success and failed scenario.
3.	Logging of the all the requests
- 	INFO log message in case print request success or failed
- 	DEBUG log message in case if data fetched, PDF and text documents are created, PDF send on the queue.
- 	ERROR log message in case of any exception and retry
4.	Exception handling


**Solution**

------------

**Apache Camel HTTP end points: **

- This will be the simplest approach as apache camel capability will be used to connect to HTTP external system. 
- An apache converter will be used to fetch details from database which then will be send to HTTP system.
- Apache camel bridge need to be deployed.

**The key solution considerations are -**
1.	HTTP End point:
- 	

2.	Apache Camel Changes:
- 	


3.	

**Logical Architecture Diagram**

------------

![HTTP stage diagram](_images/http_stage_logical_arch_diagram.png)


**Class Diagram**

------------

![HTTP stage class diagram](_images/http_stage_class_diagram.png)

**Sequence Diagram**

------------

![Reprocess sequence diagram](_images/http_stage_seq_diagram.png)
