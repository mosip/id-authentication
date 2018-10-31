# Approach for Virus scanner

**Background**

Registration packets created by the registration clients will be periodically uploaded to the server for processing. The packets will be stored in landing zone initially and status will be updated in registration status table.
The target users are
-	Server application which will process the packets
-	Administrator of the platform who may need to verify the packets
The key requirements are
-	Check the Enrolment status table for the packets for which are present in Virus Scan Folder.
-	Scan the Virus Scan Folder for the list of Packets.
-	Perform Virus Scan on the Packets using services from Core Kernel.
-	In case of successful Virus Scan, moves packets to DFS.
-	In case of Virus Scan failure, moves packets to Retry Folder.
-	Updates the status of these packets in the Enrolment Status Table.
-	Clean up the Packets from Landing Zone using Clean Up module. (IIS-006 : User Story).
-	Audit the entire process.

The key non-functional requirements are
-	Performance: Should be able to support processing multiple packet requests per second.
-	Availability: The virus scanner should keep running based on configuration so that whenever files comes in it should pick files and start processing.
-	Accessibility: the storage location should be accessible to move packets from one location to another.


**Solution**

The key solution considerations are
-	A batch job to run periodically (based on configuration) and scan the packets present in virus scan zone.
-	Read the cron configuration from properties file to schedule the job.
-	Create a tasklet and call registration status service to get list of registration ids present in virus scan zone.
-	Call file manager module get the packet and send packet to virus scanner (MOS-37 CORE KERNEL).
-	On successful virus scan -> Call FileSystemAdapter(MOS-168) and send packet to upload in CEPH.
-	On failure virus scan -> move the packet to VIRUS_SCAN_RETRY directory. (NOTE : if virus scan fails then client has to resend packet and the packet will get deleted from retry zone. It will be stored temporarily there and will get deleted periodically based on configuration).
-	Update packet status by calling RegistrationStatusService.
-	Call packetmanager module cleanup method to delete the packet from virus scan location.
-	Audit the entire transaction.


**Process Flow Diagram**

![Virus scanner process flow diagram](_images/virus_scanner_process_flow.png)

**Class Diagram**

![Virus scanner class diagram](_images/virus_scanner_process_class_diagram.png)

**Sequence Diagram**

![Virus scanner sequence diagram](_images/virus_scanner_process_seq_diagram.png)
