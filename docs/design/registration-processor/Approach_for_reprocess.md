# IN PROGRESS -  Approach for Reprocess Design 


**Background**

Registration process is assembled using number of stages, which communicate with each other by event bus. Each stage is isolated from each other and does not know about the sequence of stages in registration process. Each stage will be independently managed and deployed and will run in sequence, which will be manage by camel bridge. In case of failure or crash, system should recover back to eariler state and should process packets in case if not processed.

**The target users are -**

Server application which will process packets.
Administrator who may want to keep track of packet process.

**The key requirements are -**
-	In case of system crash or non-availability of any external system or any end point in registration processor, all stages should have capability to re process packets in case if any packet not processed successfully whenever system is back.

**Below is the behavior of the system in case of issue:**
- In case if, there is temporary issue like time out exception on REST endpoint or data base connection timed out then system should be try to resend packet. Systems maximum resend attempt are configure in configuration server.  
- In case if system crash while processing requests, system should rebuild its state and re process packets
- Due to bottleneck which could be due to any technical reason, packets will not be processed. System should have capability to reprocess them once issue is resolved. 

**The key non-functional requirements are**
1.	Auditing of the all the transactions including success and failed scenario.
2.	Logging of the all the requests
- 	INFO log message in case print request success or failed
- 	DEBUG log message in case if data fetched, PDF and text documents are created, PDF send on the queue.
- 	ERROR log message in case of any exception and retry
3.	Exception handling
4.	Performance: Should have capability to re-process messages at off peak time which will be configurable.


**Solution**

The key solution considerations are -
1.	Make below changes in REGISTRATION table:
- 	Add column called "latest_stage_name" with index on it
- 	Add column "latest_status" in  index it
- 	Add column "reprocess_count"

2.	configuration Configuration changes:
- 	Add key registration.processor.reprocess.attempt.count with value 3
- 	Add key registration.processor.reprocess.schedule.trigger.time with value in second is: 86400 (24 hours)
-   Add key registration.processor.reprocess.fetchsize with value 1000
-   Add key registration.processor.reprocess.elapse.time with value in second is: 21600


3.	Fallow below steps while processing packet in all stages which gives system capability to re-process records in case of crash:
4.	Transaction management need to be implemented properly to manage multiple data base operations in stages to ensure data integrity and consistency.

5.	Update REGISTRATION table with the values as suggested below:
    + Update column “latest_stage_name” with the value stage name
    + Update "latest_status" column with below values while processing packet:
      +  SUCCESS: In case processing successful
      +  FAILED: In case if validation fails
      +  ERROR: In case of exception while processing packet
      +  PROCESSED: This status indicate that end to end processing of packet is successful

4. Reprocess Stage 
+	Create a Vertx state: Reprocess Stage
+	Use Vertx scheduler to execute job at specific time interval. Time interval value can be fetched from configuration server which has key: registration.processor.reprocess.schedule.trigger.time 
+	Once scheduler triggered, add logic in the job to fetch all records from registration table, where status is SUCCESS or ERROR and where reprocess_count  column value less than the value configured in configuration server using key: registration.processor.reprocess.attempt.count. Also filter data by number of records using value configured in configuration server using key: registration.processor.reprocess.fetchsize and last update time "upd_dtimes" less than current time minus time configured in configuration server using key : "registration.processor.reprocess.elapse.time"
+   Once data is fetched interate through each records and update up_time for each recoard with the current system time in registration table. 
+	For each record to be reprocessed, construct data transfer object, which will be use to communicate between camel routes.   
+	Send event on the Vertx event bus to process record further in registration processor flow
+	Once event send successfully increment reprocess_count value in database by one and update it in database.
+	In case reprocess_count reach to the maximum value configured in configuration server (using in key:registration.processor.reprocess.attempt.count ) then send notification mail.



**Logical Architecture Diagram**

------------

![Reprocess logical diagram](_images/reproc_logical_arch_diagram.png)


**Class Diagram**

------------



![Reprocess class diagram](_images/reprocess_class_diagram.png)

**Sequence Diagram**

------------



![Reprocess sequence diagram](_images/reprocess_seq_diagram.png)
