# Approach for Packet Validator

**Background**
The camel bridge will send an event to registration-processor-connector stage after packet moved to packet store. The The connector stage will send an event to packet-validator-stage for further validation.

The target users are
-	Server application, which will store sync information.
-	Administrator of the platform who may need to verify the sync info.

The key requirements are
-	Packet integrity validation by comparing checksum value.
-	Validate the list of required files are present in packet before start processing.
-	Validate required applicant documents are present inside packet.

The key non-functional requirements are
-	Performance: Should validate hundreds of packets per second.



**Solution**
The key solution considerations are
-	Create Registration and Transaction entity and create dao layer for below crud operations:
o	Required files present inside packet.
o	Integrity of the packet by comparing checksum value.
o	Validate applicant document.

#####1.	Validation – 1: Validate Required files present inside packet
The decrypted packet sends packet related information inside packet_meta_info.json file. This will have information on all required files inside the hash sequence. Below are the sequence present inside packet -
1. 


#####2.	Validation – 2: Integrity of the packet by comparing checksum value
By end of sprint-3 kernel will have HMACGeneration.generatePacketDtoHash() method which takes files and sequence in which HMAC gets generated and in return it gives back the hash. Registration-processor will use same functionality to generate HMAC. The packet will have HMACFile.txt file, which has the generated hash from registration client side. Server will compare generated hash with the hash present in HMACFile.txt to check the integrity of the packet. If the packet is modified during transport the hash will not be same.
	If the validation fails then reject the packet, update status in registration-status table and send failure response to error queue.
	If the validation is successful then go to next validation (Registration machine details, officer and center details).

3.	Validation – 3: Registration machine details, officer and center details


[Download script for registration table](https://github.com/mosip/mosip/tree/DEV/design/registration-processor/_scripts/regprc-registration_v003.zip)
[Download script for registration_transaction table](https://github.com/mosip/mosip/tree/DEV/design/registration-processor/_scripts/regprc-registration_transaction_v003.zip)

**Class Diagram**
![Registration status class diagram](_images/registration_status_class_diagram.png)

**Sequence Diagram**
![Registration status sequence diagram](_images/registration_status_seq_diagram.png)
