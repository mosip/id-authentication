MOSIP Release Notes
 Registration Processor- Sprint 11
Version 1.0 | 16-May-2019

1.	Introduction
This document highlights Sprint 11 features for Registration Processor that are released for QA phase.
2.	Release Summary
Submitted by	Monobikash Das	Role/Job Title	Technical Lead
Date Raised          	16th May-2019	RFC(s) #	NA
Impact of Release 	NA	Jenkins Build #	
Implementation start date	29-APR-2019	Implementation end date	16-May-2019
Objective & Scope of Release	Sprint 11
Scope: Refer the section 3
Acceptance Criteria	Unit Testing and Code Coverage > 85%
3.	Features Delivered and Tested on Pre-QA environment

Sl No	Requirement ID	Requirement Type (New/ Enhancement/ Defect)	Description
1	MOS-21753	New	As Registration Processor, I should be able to Decrypt a Packet using Center ID and Machine ID
2	MOS-21717	New	As Registration Processor, I should be able to Fetch the Packet from DMZ and Store it in Secure Zone
3	MOS-21715	New	As Registration Processor, I should be able to Perform Virus Scan on the Encrypted Packet in Memory when I receive it from Registration Client
4	MOS-21714	New	As Registration Processor, I should be able to Perform Size Validation on the Encrypted Packet when I receive it from Registration Client
5	MOS-21713	New	As Registration Processor, I should be able to Perform Check Sum Validation on the Encrypted Packet before I Store the Packet in Packet Store
6	MOS-21712	New	As Registration Processor, I should be able to Perform Check Sum Validation on the Encrypted Packet when I receive it from Registration Client
7	MOS-21711	New	As Registration Processor, I should be able to Receive Encrypted Sync Meta Information for a Packet from Registration Client
8	MOS-21754	New	As Registration Processor, I should be able to Digitally Sign all Responses sent using a MOSIP Private Key
9	MOS-21753	New	As Registration Processor, I should be able to Decrypt a Packet using Center ID and Machine ID

4.	Prerequisites
<Dependent module/component with their respective versions should be mentioned here>
Module/Files	Component	Version	Description (If any)
Java	JDK + JRE [Installed]	Main Version: Java 8 Min Version: 181	The Java Version is to be installed in the machine.
ActiveMQ		Apache ActiveMQ 5.15.2	
Postgres-SQL		10.5	
SpringBoot		2.0.4	
HDFS 
		secure cluster ip : hdfs://52.172.51.93:51000 
	
Hadoop		client version -- 2.8.1	

5.	Open Issues
<List of Open Issues, which would be resolved or fixed in another release version, but same Sprint>
Open Items	Description
	
	
6.	Features Pending: List of Features (Requirement) which are still pending at the time of this release for current sprint only, specifying details and date/sprint in which it would be released
Sl NO	Requirement Id	Description	Future Date / Sprint when expected to release	Reason
1	MOS-23403
	Tech Story - Swagger Changes for Vertx Web Applications	Sprint 12	
2	MOS-23392	Tech Story - Mocking ABIS Queues and API changes for ABIS Requests	Sprint 12	Coding Completed Merge and unit testing is Pending on Pre-QA.
QA can test in Sprint12 another code drop
3	MOS-23299	As Bio Dedupe Stage, I should be able to process any request that comes to me based on Transaction Type and Registration Type	Sprint 12	Coding Completed Merge and unit testing is Pending on Pre-QA.
QA can test in Sprint12 another code drop
4	MOS-23298	As Demo Dedupe Stage, I should be able to process any request that comes to me based on Transaction Type and Registration Type	Sprint 12	Coding Completed Merge and unit testing is Pending on Pre-QA.
QA can test in Sprint12 another code drop
5	MOS-13140	As ABIS Handler Stage, I should be able to Create ABIS Requests and Route Camel Requests	Sprint 12	Coding Completed Merge and unit testing is Pending on Pre-QA.
QA can test in Sprint12 another code drop
6	MOS-19882	Tech Story - Performing Health Check for All Vertx  Stages before Deployment	Sprint 12	Not in scope of QA testing
7	MOS-12924	As Registration Processor, I should be able to Process an Update Packet coming from Registration Client	Sprint 12	Coding Completed Merge and unit testing is Pending on Pre-QA.
QA can test in Sprint12 another code drop
8	MOS-43	Tech Story - Security Implementation for Data and Files (encryption of DB password- config server)	Sprint 12	Not in scope of QA testing


7.	Database Changes:
Table modified: regprc.registration_list
Below are the new columns added -
1.	Packet_checksum (Datatype : varchar(128))
2.	Packet_size (Datatype : bigint) 
3.	Client_status_code (Datatype : varchar(36))
4.	Client_status_comment (Datatype : varchar(256))
5.	Additional_info (Datatype : bytearray)
