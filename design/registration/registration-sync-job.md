Design - Sync Job


**Background**

The job will run at the background of the Registration client application to sync some of the configuration from
server to client and also should push the packet and other detail captured at the client machine to server.


The **target users** are

-   System
-   Registration officer
-   Registration Supervisor

The key **requirements** are

-   Required Job should be configured to run at the specific interval based on the requirement.
-   Job should be running in the background of the application.
-   If the running job updates the configuration of the application in local machine 
    then display alert to the UI and stop capturing the data.
-   Should provide an option in the UI to trigger the sync jobs.
-   Should provide an option in the UI to trigger individual job.
-   Always the job should send the delta data to the server.
-   Always the job should pull the delta data from the server based on the last success update date and time.
-   Different state of the job should be captured.

**List of Jobs**
-   Packet Status Reader
-   Sync Master data from server
-   Sync platform configuration from server
-   Policy sync from server.

The key **non-functional requirements** are
-   The background process should not consume too much of memory and stop the UI application.
-    

**Solution**
	

Class and Sequence Diagram:
![Sync job sequence diagram](_images/registration-sync-batch-job.png)
