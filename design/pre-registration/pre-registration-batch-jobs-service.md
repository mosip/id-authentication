# Approach for Pre-Registration Batch Jobs service

**Background**
- The Pre-Registration system should support Batch job for the following Activities.

	- Updating the status from **Booked** to **Expired**.
	- Updating the status to **Processed** after Reverse Data sync.
	- Archival for the **Processed** Pre-Registrations.
	- Syncing Appointment Availability per Registration center from Master Data.

The target users are -
   - Pre-Registration system

**Solution**

**Updating the status from Booked to Expired:**

- Create a spring batch to update the status from booked to expired status once the appointment date is expired.

- The System will run a Batch Job at a certain dateTime from configuration  to update all the Pre-Registration's status on that day..

- Retrieve all pre-registration entity  which are expired from the current date to the appointment date.

- Update each retrieved pre-registration entity's status code to **expired**.

**Sequence Diagram**
![pre-registration-Expired-batch-job](_images/_sequence_diagram/batchjob-service-expired.png)



**Updating the status to Processed after Reverse Data sync:**

- Create a spring batch to update the status from booked to Processed status once the data received from registration processor.

- The System will run a Batch Job at a certain dateTime from configuration  to update all the Pre-Registration's status on that day.

- Retrieve all processed pre-registration-id-list entity which are from last processed date to till date.

- Retrieve pre-registration entity from the pre-registration id of each pre-registration-id-list entity.

- Update each retrieved pre-registration entity's status code to **Processed**.

**Sequence Diagram**
![pre-registration-Processed-batch-job](_images/_sequence_diagram/batchjob-service-consumed.png)


**Syncing appointment availability per Registration center from Master Data:**

-   Create a spring batch to archival for the Syncing Appointment Availability per Registration center from Master Data.

- The System will run a Batch Job at a certain dateTime from configuration  to syncing appointment availability per registration center.

- Call the master data service to retrieve all the registration centers. In the response system will receive the Registration center Id, working hours, processing time, holiday code, number of kiosks. Insert into the **registartion_center_details** table.

- Call the master data service for holiday details by providing the holiday code. system will receive all holiday date associated with that holiday code.

- Calculate the time slots based on the processing time for the day and insert in the **registartion_center_time_slots** table.

- link the **registartion_center_details** and **registartion_center_time_slots** tables with the registration center id.

**Sequence Diagram**
![pre-registration-Sync-batch-job](_images/_sequence_diagram/batchjob-service-availability-sync.png)


**Dependency Modules**

Component Name | Module Name | Description | 
-----|----------|-------------|
  Log        |          Kernel         |   To log the process.
  Database Access   |    Kernel      |      To get the database connectivity


**User Story References**

  **User Story No.** |  **Reference Link** |
  -----|----------|
  **MOS-1981**      |     <https://mosipid.atlassian.net/browse/MOS-1981>