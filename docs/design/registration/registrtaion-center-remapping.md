Design - Registration Center machine mapping changed 


**Background**

The registered machine to the desired center will be changed or mapped to new center. At that time the current captured data should be moved to server without any failure. This design provides the details about the process of re-mapping the machine from one center to another center.


The **target users** are

-  System.[Registration Client]
-  Registration Operator/Supervisor

The key **requirements** are

  1. Once the client machine receives details of re-mapping to a new registration center, new registration, 
     UIN update and lost UIN cannot be initiated. In-progress registrations can be completed.
     
  2. A one-time background process to push packet IDs, packets and user on-boarding data to the server will
  	 happen when the system is online AND there are no pending approval packets. It will then delete all the
  	 data except audit data. The user from the old registration center cannot login thereafter.
  	 
  3. If the one-time process has not yet run, the user will still be able to login and perform sync, EoD, export
     and upload. The user cannot perform pre-registration download and user on-boarding.

	
**Solution**

1.	As part of the Master sync using the machine ID , if the response contains the string which relevant to the 	center re-mapping or changed then we need to 	make the property[**'mosip.registrtaion.centermappedchanged'**] 	value as true. in the GLOBAL_PARAM table.

2. 	Create the CenterRemappingService - with processCenterMapped() method.
 
3. 	Once the property turns true, the application needs to verify the stauts where the current state of the 	application for the [New Registration/UIN 	Update/Lost UIN].
 
4. 	Please maintains the status flag in the session context to identify the state.
 
5. 	If the system is online and the operator is not in middle between any of the operations [New 	Registration/UIN Update/Lost UIN] then do the below process as 	sequence steps.
		1. If the EOD is off : 
			i.  Freeze the New Registration/UIN Update /Lost UIN.
			ii. Please sync the user on-boarding to the server.
			iii. Upload the packets.
			iv. Delete the all tables data except for the AUDIT table. [For Registration Packets consider only 'PROCESSING', 'PROCESSED' and 'RE-REGISTER' status]
		2. If the EOD is on:
			  Please wait until the EOD process is completed. Then repeat the above procedure.

6.  If the system is offline we should wait until the system is online and then only this process should  	initiate.		

7. 	While doing this process we should display the alert stating 
	**'Upload is going on. Please don't close the application'**. 
	
8. 	Progress bar or uploading image should be displayed in the screen and the background should be fade out. 
 
9. 	Please create the **reg_machine_center_changed.sql** and added to the module
	**registration-services** --> 	**src/main/resources**.
	
10. All events should be logged in the AUDIT table.

Packet Status from server: 

		**RECEIVED**   	:	Successfully uploaded the packet to server.Virus Scan and Decryption not yet started
		**RE-SEND**    	:	Virus Scan or Decryption failed
		**PROCESSING**	:	After Virus Scanner and Decryption successfully completed and until the UIN Generation.
		**PROCESSED**		:	UIN Generated successfully.
		**RE-REGISTER**	:	If any structural validation fails.


**Sequence and Class Diagram**
![Registered machine center changed  class and sequence diagram](_images/reg_center_machine_changed.png)
    