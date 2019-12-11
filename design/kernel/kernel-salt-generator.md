# Kernel Salt Generator Job

## 1. Background       


Kernel Salt Generator Job is a one-time job which is run to populate salts to be used to hash and encrypt data. This generic job takes schema and table name as input and generates and populates salts in the given schema and table.


***1.1.Target Users -***  
- Any module can use this job to populate salts used to hash and/or encrypt data.


***1.2. Key Functional Requirements -***   
-	Create and save salts against key indexes from 0-999 (confugurable range)


***1.3. Key Non-Functional Requirements -***   

-	Logging :
	-	Log all the exceptions along with error code and short error message    
-	Exception :
	-	Any error in storing salt details in DB should be handled with appropriate error code and message in the response  

	
### 2.	Solution    


The key solution considerations are   
- Create a project which provides a Batch Job to generate and store salts to be used while hashing and encrypting data


**2.1.	Class Diagram**   
![Class Diagram](_images/kernel-salt-generator-cd.png)   


**2.2.	Sequence Diagram**   

Any module can use kernel-salt-generator job to create and store random salt against keyIndex in respective DB.

1. 	Receive Schema name and Table Name as an input to run the job
2.	Dynamically load Schema and TableName based on given input
3.	Load Spring Batch related configurations like JobListener, JobBuilder, StepBuilder, etc.
4.	Integrate with kernel-core HMACUtil to generate salt
5.	Execute Job which creates salts in chunks (batch) and stores in the tables
6. 	If data was already present in the table, Job throws an exception and exits

Below sequence diagram shows the above sequence of operations in order to create and store salts in the database.   
![Salt Generator Sequence Diagram](_images/kernel-salt-generator-sd.png)   
